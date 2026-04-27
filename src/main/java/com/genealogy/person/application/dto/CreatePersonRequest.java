package com.genealogy.person.application.dto;

import java.time.LocalDate;

public record CreatePersonRequest(
        String fullName,
        String gender,
        LocalDate dateOfBirth
) {}
