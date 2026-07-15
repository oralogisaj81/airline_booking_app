package com.alnoor.backend.dto;

import com.alnoor.backend.model.PassengerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record PassengerRequest(
        @NotBlank String fullName,
        @NotBlank String passportNumber,
        @NotNull @Past LocalDate dateOfBirth,
        @NotNull PassengerType passengerType
) {
}
