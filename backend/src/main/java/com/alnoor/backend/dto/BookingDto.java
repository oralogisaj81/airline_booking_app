package com.alnoor.backend.dto;

import com.alnoor.backend.model.Booking;
import com.alnoor.backend.model.CabinClass;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BookingDto(
        String id,
        String pnr,
        String status,
        CabinClass cabinClass,
        int passengerCount,
        BigDecimal totalPrice,
        String contactEmail,
        String contactPhone,
        Instant createdAt,
        FlightSnapshotDto outbound,
        FlightSnapshotDto returnLeg,
        List<PassengerDto> passengers
) {
    public static BookingDto from(Booking booking) {
        return new BookingDto(
                booking.getId(), booking.getPnr(), booking.getStatus(), booking.getCabinClass(),
                booking.getPassengerCount(), booking.getTotalPrice(),
                booking.getContactEmail(), booking.getContactPhone(), booking.getCreatedAt(),
                FlightSnapshotDto.from(booking.getOutbound()), FlightSnapshotDto.from(booking.getReturnLeg()),
                booking.getPassengers().stream().map(PassengerDto::from).toList()
        );
    }
}
