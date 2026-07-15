package com.alnoor.backend.dto;

import com.alnoor.backend.model.FlightSnapshot;

import java.time.LocalDateTime;

public record FlightSnapshotDto(
        String flightId,
        String flightNumber,
        String originCode,
        String originCity,
        String destinationCode,
        String destinationCity,
        LocalDateTime departureTime,
        LocalDateTime arrivalTime,
        String aircraftType
) {
    public static FlightSnapshotDto from(FlightSnapshot snapshot) {
        if (snapshot == null || snapshot.getFlightId() == null) {
            return null;
        }
        return new FlightSnapshotDto(
                snapshot.getFlightId(), snapshot.getFlightNumber(),
                snapshot.getOriginCode(), snapshot.getOriginCity(),
                snapshot.getDestinationCode(), snapshot.getDestinationCity(),
                snapshot.getDepartureTime(), snapshot.getArrivalTime(),
                snapshot.getAircraftType()
        );
    }
}
