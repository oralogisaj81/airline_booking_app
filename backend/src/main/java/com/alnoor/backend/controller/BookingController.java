package com.alnoor.backend.controller;

import com.alnoor.backend.config.ApiException;
import com.alnoor.backend.dto.BookingDto;
import com.alnoor.backend.dto.CreateBookingRequest;
import com.alnoor.backend.dto.PassengerRequest;
import com.alnoor.backend.model.Booking;
import com.alnoor.backend.model.CabinClass;
import com.alnoor.backend.model.Flight;
import com.alnoor.backend.model.Passenger;
import com.alnoor.backend.repository.BookingRepository;
import com.alnoor.backend.repository.FlightRepository;
import com.alnoor.backend.repository.UserRepository;
import com.alnoor.backend.security.AppUserDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private static final String PNR_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no 0/O/1/I
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String[] SEAT_LETTERS = {"A", "B", "C", "D", "E", "F"};

    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public BookingController(FlightRepository flightRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<BookingDto> createBooking(@Valid @RequestBody CreateBookingRequest request,
                                                      @AuthenticationPrincipal AppUserDetails principal) {
        boolean roundTrip = request.returnFlightId() != null && !request.returnFlightId().isBlank();

        // Lock every distinct flight touched by this booking, sorted by id,
        // so two concurrent bookings that both involve the same pair of
        // flights always acquire their locks in the same order (avoids
        // deadlock) -- same pattern as locking every distinct product id in
        // a multi-item cart checkout.
        List<String> idsToLock = (roundTrip
                ? Stream.of(request.outboundFlightId(), request.returnFlightId()).distinct()
                : Stream.of(request.outboundFlightId())).sorted().toList();

        Map<String, Flight> locked = new HashMap<>();
        for (String id : idsToLock) {
            Flight flight = flightRepository.findByIdForUpdate(id)
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found: " + id));
            locked.put(id, flight);
        }

        Flight outboundFlight = locked.get(request.outboundFlightId());
        Flight returnFlight = roundTrip ? locked.get(request.returnFlightId()) : null;

        int passengerCount = request.passengers().size();
        CabinClass cabinClass = request.cabinClass();

        if (outboundFlight.getAvailableSeats(cabinClass) < passengerCount) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Only " + outboundFlight.getAvailableSeats(cabinClass) + " " + cabinClass
                            + " seat(s) left on flight " + outboundFlight.getFlightNumber() + ".");
        }
        if (returnFlight != null && returnFlight.getAvailableSeats(cabinClass) < passengerCount) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Only " + returnFlight.getAvailableSeats(cabinClass) + " " + cabinClass
                            + " seat(s) left on flight " + returnFlight.getFlightNumber() + ".");
        }

        outboundFlight.decreaseSeats(cabinClass, passengerCount);
        if (returnFlight != null) {
            returnFlight.decreaseSeats(cabinClass, passengerCount);
        }

        BigDecimal legPrice = outboundFlight.getPriceFor(cabinClass)
                .add(returnFlight != null ? returnFlight.getPriceFor(cabinClass) : BigDecimal.ZERO);
        BigDecimal totalPrice = legPrice.multiply(BigDecimal.valueOf(passengerCount));

        Booking booking = new Booking(UUID.randomUUID().toString(), generateUniquePnr(),
                userRepository.getReferenceById(principal.getId()), outboundFlight, returnFlight,
                cabinClass, passengerCount, totalPrice,
                request.contactEmail().trim().toLowerCase(), request.contactPhone().trim());

        int seatIndex = 0;
        for (PassengerRequest p : request.passengers()) {
            booking.addPassenger(new Passenger(UUID.randomUUID().toString(), p.fullName().trim(),
                    p.passportNumber().trim().toUpperCase(), p.dateOfBirth(), p.passengerType(),
                    assignSeat(cabinClass, seatIndex++), p.mealPreference()));
        }

        bookingRepository.saveAndFlush(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(BookingDto.from(booking));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<BookingDto> myBookings(@AuthenticationPrincipal AppUserDetails principal) {
        return bookingRepository.findByCustomer_IdOrderByCreatedAtDesc(principal.getId())
                .stream().map(BookingDto::from).toList();
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public BookingDto getBooking(@PathVariable String id, @AuthenticationPrincipal AppUserDetails principal) {
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getCustomer().getId().equals(principal.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));
        return BookingDto.from(booking);
    }

    @PatchMapping("/{id}/cancel")
    @Transactional
    public BookingDto cancelBooking(@PathVariable String id, @AuthenticationPrincipal AppUserDetails principal) {
        Booking booking = bookingRepository.findById(id)
                .filter(b -> b.getCustomer().getId().equals(principal.getId()))
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!"CANCELLED".equals(booking.getStatus())) {
            booking.setStatus("CANCELLED");

            List<String> idsToLock = (booking.getReturnFlight() != null
                    ? Stream.of(booking.getOutboundFlight().getId(), booking.getReturnFlight().getId()).distinct()
                    : Stream.of(booking.getOutboundFlight().getId())).sorted().toList();

            for (String flightId : idsToLock) {
                flightRepository.findByIdForUpdate(flightId)
                        .ifPresent(flight -> flight.increaseSeats(booking.getCabinClass(), booking.getPassengerCount()));
            }
        }
        return BookingDto.from(booking);
    }

    private String generateUniquePnr() {
        String pnr;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(PNR_ALPHABET.charAt(RANDOM.nextInt(PNR_ALPHABET.length())));
            }
            pnr = sb.toString();
        } while (bookingRepository.existsByPnr(pnr));
        return pnr;
    }

    private String assignSeat(CabinClass cabinClass, int index) {
        int baseRow = switch (cabinClass) {
            case FIRST -> 1;
            case BUSINESS -> 10;
            case ECONOMY -> 25;
        };
        int row = baseRow + index / SEAT_LETTERS.length;
        return row + SEAT_LETTERS[index % SEAT_LETTERS.length];
    }
}
