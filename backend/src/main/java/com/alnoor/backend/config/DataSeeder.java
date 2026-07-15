package com.alnoor.backend.config;

import com.alnoor.backend.model.Airport;
import com.alnoor.backend.model.Flight;
import com.alnoor.backend.repository.AirportRepository;
import com.alnoor.backend.repository.FlightRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Seeds a rolling window of flights starting "today" -- fine for a
// demo/portfolio deployment, but flights fall out of the search window as
// real time passes since this only runs once (idempotent, see below).
@Component
public class DataSeeder implements CommandLineRunner {

    private static final int DAYS_AHEAD = 45;

    private final AirportRepository airportRepository;
    private final FlightRepository flightRepository;

    public DataSeeder(AirportRepository airportRepository, FlightRepository flightRepository) {
        this.airportRepository = airportRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public void run(String... args) {
        if (airportRepository.count() == 0) {
            airportRepository.saveAll(List.of(
                    new Airport("DXB", "Dubai", "United Arab Emirates"),
                    new Airport("JFK", "New York", "United States"),
                    new Airport("LHR", "London", "United Kingdom"),
                    new Airport("SYD", "Sydney", "Australia"),
                    new Airport("SIN", "Singapore", "Singapore"),
                    new Airport("BOM", "Mumbai", "India"),
                    new Airport("CDG", "Paris", "France")
            ));
        }

        if (flightRepository.count() > 0) {
            return;
        }

        List<RouteTemplate> routes = List.of(
                new RouteTemplate("AN201", "DXB", "Dubai", "JFK", "New York", LocalTime.of(3, 15), 890,
                        "Airbus A380-800", "1450", "5200", "12500", 350, 76, 14),
                new RouteTemplate("AN202", "JFK", "New York", "DXB", "Dubai", LocalTime.of(22, 40), 755,
                        "Airbus A380-800", "1450", "5200", "12500", 350, 76, 14),
                new RouteTemplate("AN203", "DXB", "Dubai", "LHR", "London", LocalTime.of(8, 35), 455,
                        "Boeing 777-300ER", "890", "3200", "7800", 304, 58, 8),
                new RouteTemplate("AN204", "LHR", "London", "DXB", "Dubai", LocalTime.of(14, 45), 425,
                        "Boeing 777-300ER", "890", "3200", "7800", 304, 58, 8),
                new RouteTemplate("AN205", "DXB", "Dubai", "SYD", "Sydney", LocalTime.of(2, 20), 835,
                        "Airbus A380-800", "1650", "6100", "14200", 350, 76, 14),
                new RouteTemplate("AN206", "SYD", "Sydney", "DXB", "Dubai", LocalTime.of(21, 15), 875,
                        "Airbus A380-800", "1650", "6100", "14200", 350, 76, 14),
                new RouteTemplate("AN207", "DXB", "Dubai", "SIN", "Singapore", LocalTime.of(9, 10), 445,
                        "Boeing 777-300ER", "780", "2800", "6900", 304, 58, 8),
                new RouteTemplate("AN208", "SIN", "Singapore", "DXB", "Dubai", LocalTime.of(23, 50), 470,
                        "Boeing 777-300ER", "780", "2800", "6900", 304, 58, 8),
                new RouteTemplate("AN209", "DXB", "Dubai", "BOM", "Mumbai", LocalTime.of(10, 20), 190,
                        "Airbus A350-900", "340", "1150", "2600", 270, 44, 6),
                new RouteTemplate("AN210", "BOM", "Mumbai", "DXB", "Dubai", LocalTime.of(14, 10), 185,
                        "Airbus A350-900", "340", "1150", "2600", 270, 44, 6),
                new RouteTemplate("AN211", "DXB", "Dubai", "CDG", "Paris", LocalTime.of(8, 50), 440,
                        "Boeing 777-300ER", "820", "3000", "7200", 304, 58, 8),
                new RouteTemplate("AN212", "CDG", "Paris", "DXB", "Dubai", LocalTime.of(15, 40), 475,
                        "Boeing 777-300ER", "820", "3000", "7200", 304, 58, 8)
        );

        LocalDate today = LocalDate.now();
        List<Flight> flights = new ArrayList<>();
        for (int day = 0; day < DAYS_AHEAD; day++) {
            LocalDate date = today.plusDays(day);
            for (RouteTemplate route : routes) {
                flights.add(route.toFlight(date));
            }
        }
        flightRepository.saveAll(flights);
    }

    private record RouteTemplate(
            String flightNumber, String originCode, String originCity, String destinationCode, String destinationCity,
            LocalTime departureLocalTime, int durationMinutes, String aircraftType,
            String economyPrice, String businessPrice, String firstPrice,
            int economySeats, int businessSeats, int firstSeats
    ) {
        Flight toFlight(LocalDate date) {
            var departureTime = date.atTime(departureLocalTime);
            var arrivalTime = departureTime.plusMinutes(durationMinutes);
            return new Flight(
                    UUID.randomUUID().toString(), flightNumber, originCode, originCity, destinationCode, destinationCity,
                    departureTime, arrivalTime, durationMinutes, aircraftType,
                    new BigDecimal(economyPrice), new BigDecimal(businessPrice), new BigDecimal(firstPrice),
                    economySeats, businessSeats, firstSeats
            );
        }
    }
}
