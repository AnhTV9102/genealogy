package com.genealogy.person.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CreatePersonRequest(
    @NotBlank(message = "Full name is required")
        @Size(min = 1, max = 255, message = "Full name must be between 1 and 255 characters")
        String fullName,
    @NotBlank(message = "Gender is required") String gender,
    @Past(message = "Date of birth must be in the past") LocalDate dateOfBirth) {}
