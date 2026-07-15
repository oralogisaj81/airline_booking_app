package com.alnoor.backend.repository;

import com.alnoor.backend.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AirportRepository extends JpaRepository<Airport, String> {

    List<Airport> findAllByOrderByCityAsc();
}
