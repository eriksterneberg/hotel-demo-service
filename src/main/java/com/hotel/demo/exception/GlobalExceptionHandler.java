package com.hotel.demo.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error responses following RFC 7807 Problem Details.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(InvalidEmailException.class)
    public ProblemDetail handleInvalidEmail(InvalidEmailException ex) {
        log.warn("Invalid email format: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/invalid-email-format"));
        problem.setTitle("Invalid Email Format");
        problem.setProperty("errorCode", "INVALID_EMAIL_FORMAT");
        return problem;
    }
    
    @ExceptionHandler(DatabaseConnectionException.class)
    public ProblemDetail handleDatabaseConnection(DatabaseConnectionException ex) {
        log.error("Database connection error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.SERVICE_UNAVAILABLE,
            "Unable to connect to the database. Please try again later."
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/database-connection-error"));
        problem.setTitle("Database Connection Error");
        problem.setProperty("errorCode", "DATABASE_CONNECTION_ERROR");
        return problem;
    }
    
    @ExceptionHandler(SearchTimeoutException.class)
    public ProblemDetail handleSearchTimeout(SearchTimeoutException ex) {
        log.warn("Search timeout: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.GATEWAY_TIMEOUT,
            ex.getMessage()
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/search-timeout"));
        problem.setTitle("Search Timeout");
        problem.setProperty("errorCode", "SEARCH_TIMEOUT");
        return problem;
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        String detail = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .reduce((a, b) -> a + ", " + b)
            .orElse("Validation failed");
        
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            detail
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/validation-error"));
        problem.setTitle("Validation Error");
        problem.setProperty("errorCode", "VALIDATION_ERROR");
        return problem;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Constraint violation: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            ex.getMessage()
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/constraint-violation"));
        problem.setTitle("Constraint Violation");
        problem.setProperty("errorCode", "CONSTRAINT_VIOLATION");
        return problem;
    }
    
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred while processing the search"
        );
        problem.setType(URI.create("https://hotel-demo.com/errors/internal-server-error"));
        problem.setTitle("Internal Server Error");
        problem.setProperty("errorCode", "INTERNAL_SERVER_ERROR");
        return problem;
    }
}
