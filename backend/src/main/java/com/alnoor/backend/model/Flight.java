package com.alnoor.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// departureTime/arrivalTime are local wall-clock time at the respective
// airport (not UTC-normalized) -- a deliberate simplification so
// search-by-date and display don't need a real timezone/DST database.
@Entity
@Table(name = "flights")
public class Flight {

    @Id
    private String id;

    @Column(nullable = false)
    private String flightNumber;

    @Column(nullable = false)
    private String originCode;

    @Column(nullable = false)
    private String originCity;

    @Column(nullable = false)
    private String destinationCode;

    @Column(nullable = false)
    private String destinationCity;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private String aircraftType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal economyPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal businessPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal firstPrice;

    @Column(nullable = false)
    private int economySeatsTotal;

    @Column(nullable = false)
    private int economySeatsAvailable;

    @Column(nullable = false)
    private int businessSeatsTotal;

    @Column(nullable = false)
    private int businessSeatsAvailable;

    @Column(nullable = false)
    private int firstSeatsTotal;

    @Column(nullable = false)
    private int firstSeatsAvailable;

    protected Flight() {
    }

    public Flight(String id, String flightNumber, String originCode, String originCity,
                  String destinationCode, String destinationCity, LocalDateTime departureTime,
                  LocalDateTime arrivalTime, int durationMinutes, String aircraftType,
                  BigDecimal economyPrice, BigDecimal businessPrice, BigDecimal firstPrice,
                  int economySeatsTotal, int businessSeatsTotal, int firstSeatsTotal) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.originCode = originCode;
        this.originCity = originCity;
        this.destinationCode = destinationCode;
        this.destinationCity = destinationCity;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.durationMinutes = durationMinutes;
        this.aircraftType = aircraftType;
        this.economyPrice = economyPrice;
        this.businessPrice = businessPrice;
        this.firstPrice = firstPrice;
        this.economySeatsTotal = economySeatsTotal;
        this.economySeatsAvailable = economySeatsTotal;
        this.businessSeatsTotal = businessSeatsTotal;
        this.businessSeatsAvailable = businessSeatsTotal;
        this.firstSeatsTotal = firstSeatsTotal;
        this.firstSeatsAvailable = firstSeatsTotal;
    }

    public int getAvailableSeats(CabinClass cabinClass) {
        return switch (cabinClass) {
            case ECONOMY -> economySeatsAvailable;
            case BUSINESS -> businessSeatsAvailable;
            case FIRST -> firstSeatsAvailable;
        };
    }

    public BigDecimal getPriceFor(CabinClass cabinClass) {
        return switch (cabinClass) {
            case ECONOMY -> economyPrice;
            case BUSINESS -> businessPrice;
            case FIRST -> firstPrice;
        };
    }

    public void decreaseSeats(CabinClass cabinClass, int count) {
        switch (cabinClass) {
            case ECONOMY -> economySeatsAvailable -= count;
            case BUSINESS -> businessSeatsAvailable -= count;
            case FIRST -> firstSeatsAvailable -= count;
        }
    }

    public void increaseSeats(CabinClass cabinClass, int count) {
        switch (cabinClass) {
            case ECONOMY -> economySeatsAvailable = Math.min(economySeatsTotal, economySeatsAvailable + count);
            case BUSINESS -> businessSeatsAvailable = Math.min(businessSeatsTotal, businessSeatsAvailable + count);
            case FIRST -> firstSeatsAvailable = Math.min(firstSeatsTotal, firstSeatsAvailable + count);
        }
    }

    public String getId() {
        return id;
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

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public BigDecimal getEconomyPrice() {
        return economyPrice;
    }

    public BigDecimal getBusinessPrice() {
        return businessPrice;
    }

    public BigDecimal getFirstPrice() {
        return firstPrice;
    }

    public int getEconomySeatsAvailable() {
        return economySeatsAvailable;
    }

    public int getBusinessSeatsAvailable() {
        return businessSeatsAvailable;
    }

    public int getFirstSeatsAvailable() {
        return firstSeatsAvailable;
    }
}
