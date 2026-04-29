package com.genealogy.person.application.dto;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record UpdatePersonRequest(
    @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
        String fullName,
    @PastOrPresent(message = "Date of death cannot be in the future") LocalDate dateOfDeath) {}
