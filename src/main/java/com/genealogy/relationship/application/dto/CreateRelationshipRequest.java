package com.genealogy.relationship.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateRelationshipRequest(
    @NotNull(message = "From person ID is required")
        @Positive(message = "From person ID must be positive")
        Long fromPersonId,
    @NotNull(message = "To person ID is required")
        @Positive(message = "To person ID must be positive")
        Long toPersonId,
    @NotBlank(message = "Relationship type is required") String type) {}
