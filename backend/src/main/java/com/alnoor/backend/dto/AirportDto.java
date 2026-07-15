package com.alnoor.backend.dto;

import com.alnoor.backend.model.Airport;

public record AirportDto(String code, String city, String country) {
    public static AirportDto from(Airport airport) {
        return new AirportDto(airport.getCode(), airport.getCity(), airport.getCountry());
    }
}
