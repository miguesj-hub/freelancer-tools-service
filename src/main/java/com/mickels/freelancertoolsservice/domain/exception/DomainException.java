package com.mickels.freelancertoolsservice.domain.exception;

/**
 * Base type for all domain-level errors. Framework-free (Clean Architecture:
 * the domain layer must not depend on Spring/JPA).
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}
