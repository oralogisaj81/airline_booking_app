package com.alnoor.backend.repository;

import com.alnoor.backend.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByCustomer_IdOrderByCreatedAtDesc(String customerId);

    boolean existsByPnr(String pnr);
}
