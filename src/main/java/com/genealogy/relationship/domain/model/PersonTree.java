package com.genealogy.relationship.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Domain model for ancestor-descendant relationships.
 * This is part of the closure table pattern for efficient tree traversal.
 */
@Getter
@AllArgsConstructor
public class PersonTree {
    private final Long ancestorId;
    private final Long descendantId;
    private final Integer depth;

    /**
     * Creates a self-relationship (person as their own ancestor with depth 0).
     */
    public static PersonTree createSelfRelation(Long personId) {
        return new PersonTree(personId, personId, 0);
    }

    /**
     * Creates a direct parent-child relationship (depth 1).
     */
    public static PersonTree createDirectRelation(Long ancestorId, Long descendantId) {
        return new PersonTree(ancestorId, descendantId, 1);
    }

    /**
     * Creates an indirect relationship with specified depth.
     */
    public static PersonTree create(Long ancestorId, Long descendantId, Integer depth) {
        return new PersonTree(ancestorId, descendantId, depth);
    }

    /**
     * Checks if this is a self-relationship (depth = 0).
     */
    public boolean isSelfRelation() {
        return depth == 0;
    }

    /**
     * Checks if this is a direct relationship (depth = 1).
     */
    public boolean isDirectRelation() {
        return depth == 1;
    }
}

