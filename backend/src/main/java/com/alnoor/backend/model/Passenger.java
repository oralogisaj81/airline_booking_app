package com.alnoor.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "passengers")
public class Passenger {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String passportNumber;

    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PassengerType passengerType;

    // Cosmetic only -- assigned at booking time for the receipt, not backed
    // by a real seat map or checked for collisions across bookings.
    @Column
    private String seatNumber;

    protected Passenger() {
    }

    public Passenger(String id, String fullName, String passportNumber, LocalDate dateOfBirth,
                      PassengerType passengerType, String seatNumber) {
        this.id = id;
        this.fullName = fullName;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.passengerType = passengerType;
        this.seatNumber = seatNumber;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public PassengerType getPassengerType() {
        return passengerType;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
}
