package com.mickels.freelancertoolsservice.domain.exception;

/**
 * Raised when deletion is rejected because dependent records still exist,
 * protecting historical data (FR-013). Maps to HTTP 409.
 */
public class DependentRecordsException extends DomainException {

    public DependentRecordsException(String message) {
        super(message);
    }
}
