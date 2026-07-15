package com.alnoor.backend.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", uniqueConstraints = @UniqueConstraint(columnNames = "pnr"))
public class Booking {

    @Id
    private String id;

    @Column(nullable = false)
    private String pnr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    // Kept for the pessimistic-lock seat restore on cancellation; display
    // data comes from the snapshot fields below, not this association.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outbound_flight_id", nullable = false)
    private Flight outboundFlight;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_flight_id")
    private Flight returnFlight;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "flightId", column = @Column(name = "outbound_flight_id_snapshot")),
            @AttributeOverride(name = "flightNumber", column = @Column(name = "outbound_flight_number")),
            @AttributeOverride(name = "originCode", column = @Column(name = "outbound_origin_code")),
            @AttributeOverride(name = "originCity", column = @Column(name = "outbound_origin_city")),
            @AttributeOverride(name = "destinationCode", column = @Column(name = "outbound_destination_code")),
            @AttributeOverride(name = "destinationCity", column = @Column(name = "outbound_destination_city")),
            @AttributeOverride(name = "departureTime", column = @Column(name = "outbound_departure_time")),
            @AttributeOverride(name = "arrivalTime", column = @Column(name = "outbound_arrival_time")),
            @AttributeOverride(name = "aircraftType", column = @Column(name = "outbound_aircraft_type")),
    })
    private FlightSnapshot outbound;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "flightId", column = @Column(name = "return_flight_id_snapshot")),
            @AttributeOverride(name = "flightNumber", column = @Column(name = "return_flight_number")),
            @AttributeOverride(name = "originCode", column = @Column(name = "return_origin_code")),
            @AttributeOverride(name = "originCity", column = @Column(name = "return_origin_city")),
            @AttributeOverride(name = "destinationCode", column = @Column(name = "return_destination_code")),
            @AttributeOverride(name = "destinationCity", column = @Column(name = "return_destination_city")),
            @AttributeOverride(name = "departureTime", column = @Column(name = "return_departure_time")),
            @AttributeOverride(name = "arrivalTime", column = @Column(name = "return_arrival_time")),
            @AttributeOverride(name = "aircraftType", column = @Column(name = "return_aircraft_type")),
    })
    private FlightSnapshot returnLeg;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CabinClass cabinClass;

    @Column(nullable = false)
    private int passengerCount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private String contactEmail;

    @Column(nullable = false)
    private String contactPhone;

    @Column(nullable = false)
    private String status; // "CONFIRMED" | "CANCELLED"

    @Column(nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "passenger_index")
    private List<Passenger> passengers = new ArrayList<>();

    protected Booking() {
    }

    public Booking(String id, String pnr, User customer, Flight outboundFlight, Flight returnFlight,
                    CabinClass cabinClass, int passengerCount, BigDecimal totalPrice,
                    String contactEmail, String contactPhone) {
        this.id = id;
        this.pnr = pnr;
        this.customer = customer;
        this.outboundFlight = outboundFlight;
        this.returnFlight = returnFlight;
        this.outbound = new FlightSnapshot(outboundFlight);
        this.returnLeg = returnFlight != null ? new FlightSnapshot(returnFlight) : null;
        this.cabinClass = cabinClass;
        this.passengerCount = passengerCount;
        this.totalPrice = totalPrice;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.status = "CONFIRMED";
        this.createdAt = Instant.now();
    }

    public void addPassenger(Passenger passenger) {
        passengers.add(passenger);
        passenger.setBooking(this);
    }

    public String getId() {
        return id;
    }

    public String getPnr() {
        return pnr;
    }

    public User getCustomer() {
        return customer;
    }

    public Flight getOutboundFlight() {
        return outboundFlight;
    }

    public Flight getReturnFlight() {
        return returnFlight;
    }

    public FlightSnapshot getOutbound() {
        return outbound;
    }

    public FlightSnapshot getReturnLeg() {
        return returnLeg;
    }

    public CabinClass getCabinClass() {
        return cabinClass;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }
}
