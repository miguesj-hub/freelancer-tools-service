package com.mickels.freelancertoolsservice.infrastructure.config;

import com.mickels.freelancertoolsservice.api.model.ProblemDetail;
import com.mickels.freelancertoolsservice.api.model.ProblemDetailErrorsInner;
import com.mickels.freelancertoolsservice.domain.exception.DependentRecordsException;
import com.mickels.freelancertoolsservice.domain.exception.EntityNotFoundException;
import com.mickels.freelancertoolsservice.domain.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Maps domain and validation errors to the generated {@link ProblemDetail} DTO
 * (T007): 404 not-found, 409 dependent-records, 400 validation.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    ResponseEntity<ProblemDetail> handleNotFound(EntityNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Resource not found", ex.getMessage());
    }

    @ExceptionHandler(DependentRecordsException.class)
    ResponseEntity<ProblemDetail> handleConflict(DependentRecordsException ex) {
        return build(HttpStatus.CONFLICT, "Operation rejected", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    ResponseEntity<ProblemDetail> handleValidation(ValidationException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid input", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleBeanValidation(MethodArgumentNotValidException ex) {
        ProblemDetail body = problem(HttpStatus.BAD_REQUEST, "Invalid input", "Request validation failed");
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                body.addErrorsItem(new ProblemDetailErrorsInner()
                        .field(fe.getField())
                        .message(fe.getDefaultMessage())));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ResponseEntity<ProblemDetail> build(HttpStatus status, String title, String detail) {
        return ResponseEntity.status(status).body(problem(status, title, detail));
    }

    private ProblemDetail problem(HttpStatus status, String title, String detail) {
        return new ProblemDetail()
                .status(status.value())
                .title(title)
                .detail(detail);
    }
}
