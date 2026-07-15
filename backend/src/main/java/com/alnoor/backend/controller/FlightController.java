package com.alnoor.backend.controller;

import com.alnoor.backend.config.ApiException;
import com.alnoor.backend.dto.FlightDto;
import com.alnoor.backend.repository.FlightRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightRepository flightRepository;

    public FlightController(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    // Public -- a shopper needs to see flights and fares before signing in.
    @GetMapping("/search")
    public List<FlightDto> search(@RequestParam String origin,
                                   @RequestParam String destination,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
        return flightRepository.search(origin.trim().toUpperCase(), destination.trim().toUpperCase(), dayStart, dayEnd)
                .stream().map(FlightDto::from).toList();
    }

    @GetMapping("/{id}")
    public FlightDto get(@PathVariable String id) {
        return flightRepository.findById(id)
                .map(FlightDto::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Flight not found"));
    }
}
