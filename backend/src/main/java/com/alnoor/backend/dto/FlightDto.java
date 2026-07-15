package com.alnoor.backend.dto;

import com.alnoor.backend.model.Flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightDto(
        String id,
        String flightNumber,
        String originCode,
        String originCity,
        String destinationCode,
        String destinationCity,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        int durationMinutes,
        String aircraftType,
        BigDecimal economyPrice,
        BigDecimal businessPrice,
        BigDecimal firstPrice,
        int economySeatsAvailable,
        int businessSeatsAvailable,
        int firstSeatsAvailable
) {
    public static FlightDto from(Flight flight) {
        return new FlightDto(
                flight.getId(), flight.getFlightNumber(),
                flight.getOriginCode(), flight.getOriginCity(),
                flight.getDestinationCode(), flight.getDestinationCity(),
                flight.getDepartureTime(), flight.getArrivalTime(),
                flight.getDurationMinutes(), flight.getAircraftType(),
                flight.getEconomyPrice(), flight.getBusinessPrice(), flight.getFirstPrice(),
                flight.getEconomySeatsAvailable(), flight.getBusinessSeatsAvailable(), flight.getFirstSeatsAvailable()
        );
    }
}
