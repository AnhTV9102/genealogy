package com.genealogy.relationship.application.dto;

import java.time.LocalDateTime;

public record RelationshipResponse(
    Long id, Long fromPersonId, Long toPersonId, String type, LocalDateTime createdAt) {}
