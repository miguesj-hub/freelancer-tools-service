package com.mickels.freelancertoolsservice.domain.exception;

/** Raised when domain invariants are violated (maps to HTTP 400). */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message);
    }
}
