package com.alnoor.backend.controller;

import com.alnoor.backend.dto.AirportDto;
import com.alnoor.backend.repository.AirportRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    private final AirportRepository airportRepository;

    public AirportController(AirportRepository airportRepository) {
        this.airportRepository = airportRepository;
    }

    // Public, cheap, unauthenticated -- also used as the platform health check.
    @GetMapping
    public List<AirportDto> list() {
        return airportRepository.findAllByOrderByCityAsc().stream().map(AirportDto::from).toList();
    }
}
