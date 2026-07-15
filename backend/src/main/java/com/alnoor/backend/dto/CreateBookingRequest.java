package com.alnoor.backend.dto;

import com.alnoor.backend.model.CabinClass;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateBookingRequest(
        @NotBlank String outboundFlightId,
        String returnFlightId,
        @NotNull CabinClass cabinClass,
        @NotBlank @Email String contactEmail,
        @NotBlank String contactPhone,
        @NotEmpty @Valid List<PassengerRequest> passengers
) {
}
