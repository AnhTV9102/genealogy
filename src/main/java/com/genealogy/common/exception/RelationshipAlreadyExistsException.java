package com.genealogy.common.exception;

public class RelationshipAlreadyExistsException extends RuntimeException {
    public RelationshipAlreadyExistsException() {
        super("Relationship already exists between these persons");
    }
}
