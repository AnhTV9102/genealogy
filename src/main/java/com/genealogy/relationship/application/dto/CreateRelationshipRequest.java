package com.genealogy.relationship.application.dto;

public record CreateRelationshipRequest(
        Long fromPersonId,
        Long toPersonId,
        String type
) {}
