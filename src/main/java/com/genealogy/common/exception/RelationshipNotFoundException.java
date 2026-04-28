package com.genealogy.common.exception;

public class RelationshipNotFoundException extends RuntimeException {
    public RelationshipNotFoundException(Long id) {
        super("Relationship not found with id: " + id);
    }
}
