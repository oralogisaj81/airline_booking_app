package com.alnoor.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

// Snapshots the flight's display fields onto the booking at creation time,
// so a booking's receipt stays accurate even if the underlying Flight row
// is ever edited later (see the snapshot-field convention in SKILL.md).
@Embeddable
public class FlightSnapshot {

    @Column
    private String flightId;

    @Column
    private String flightNumber;

    @Column
    private String originCode;

    @Column
    private String originCity;

    @Column
    private String destinationCode;

    @Column
    private String destinationCity;

    @Column
    private LocalDateTime departureTime;

    @Column
    private LocalDateTime arrivalTime;

    @Column
    private String aircraftType;

    protected FlightSnapshot() {
    }

    public FlightSnapshot(Flight flight) {
        this.flightId = flight.getId();
        this.flightNumber = flight.getFlightNumber();
        this.originCode = flight.getOriginCode();
        this.originCity = flight.getOriginCity();
        this.destinationCode = flight.getDestinationCode();
        this.destinationCity = flight.getDestinationCity();
        this.departureTime = flight.getDepartureTime();
        this.arrivalTime = flight.getArrivalTime();
        this.aircraftType = flight.getAircraftType();
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOriginCode() {
        return originCode;
    }

    public String getOriginCity() {
        return originCity;
    }

    public String getDestinationCode() {
        return destinationCode;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public String getAircraftType() {
        return aircraftType;
    }
}
