package com.mickels.freelancertoolsservice.domain.exception;

/** Raised when a referenced entity does not exist (maps to HTTP 404). */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entity, Object id) {
        super(entity + " not found: " + id);
    }
}
