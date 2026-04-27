package com.genealogy.person.application.dto;

import java.time.LocalDate;

public record UpdatePersonRequest(
        String fullName,
        LocalDate dateOfDeath
) {}
