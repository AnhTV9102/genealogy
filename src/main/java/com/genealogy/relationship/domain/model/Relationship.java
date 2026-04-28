package com.genealogy.relationship.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Relationship {

    private Long id;
    private Long fromPersonId;
    private Long toPersonId;
    private RelationshipType type;
    private LocalDateTime createdAt;

    private Relationship(Long id,
                        Long fromPersonId,
                        Long toPersonId,
                        RelationshipType type,
                        LocalDateTime createdAt) {

        validateRelationship(fromPersonId, toPersonId, type);

        this.id = id;
        this.fromPersonId = fromPersonId;
        this.toPersonId = toPersonId;
        this.type = type;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // 👉 Factory method (create mới)
    public static Relationship create(Long fromPersonId,
                                     Long toPersonId,
                                     RelationshipType type) {

        return new Relationship(
                null,
                fromPersonId,
                toPersonId,
                type,
                null
        );
    }

    // 👉 Rehydrate từ DB
    public static Relationship restore(Long id,
                                      Long fromPersonId,
                                      Long toPersonId,
                                      RelationshipType type,
                                      LocalDateTime createdAt) {

        return new Relationship(id, fromPersonId, toPersonId, type, createdAt);
    }

    // =========================
    // BUSINESS METHODS
    // =========================

    public boolean isParentChild() {
        return type == RelationshipType.PARENT_OF || type == RelationshipType.CHILD_OF;
    }

    public boolean isSpouse() {
        return type == RelationshipType.SPOUSE_OF;
    }

    public boolean isAdoption() {
        return type == RelationshipType.ADOPTED_PARENT_OF;
    }

    // =========================
    // VALIDATION
    // =========================

    private void validateRelationship(Long fromPersonId, Long toPersonId, RelationshipType type) {
        Objects.requireNonNull(fromPersonId, "From person ID cannot be null");
        Objects.requireNonNull(toPersonId, "To person ID cannot be null");
        Objects.requireNonNull(type, "Relationship type cannot be null");

        if (fromPersonId.equals(toPersonId)) {
            throw new IllegalArgumentException("Person cannot have relationship with themselves");
        }
    }

    // =========================
    // GETTERS (read-only)
    // =========================

    public Long getId() {
        return id;
    }

    public Long getFromPersonId() {
        return fromPersonId;
    }

    public Long getToPersonId() {
        return toPersonId;
    }

    public RelationshipType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
