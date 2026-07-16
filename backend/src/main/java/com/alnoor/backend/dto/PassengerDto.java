package com.alnoor.backend.dto;

import com.alnoor.backend.model.MealPreference;
import com.alnoor.backend.model.Passenger;
import com.alnoor.backend.model.PassengerType;

import java.time.LocalDate;

public record PassengerDto(
        String id,
        String fullName,
        String passportNumber,
        LocalDate dateOfBirth,
        PassengerType passengerType,
        String seatNumber,
        MealPreference mealPreference
) {
    public static PassengerDto from(Passenger passenger) {
        return new PassengerDto(
                passenger.getId(), passenger.getFullName(), passenger.getPassportNumber(),
                passenger.getDateOfBirth(), passenger.getPassengerType(), passenger.getSeatNumber(),
                // Legacy rows created before this field existed have a null value in the DB.
                passenger.getMealPreference() != null ? passenger.getMealPreference() : MealPreference.NONE
        );
    }
}
