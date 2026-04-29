package com.genealogy.person.application.dto;

import java.time.LocalDate;

public record PersonResponse(Long id, String fullName, String gender, LocalDate dateOfBirth) {}
