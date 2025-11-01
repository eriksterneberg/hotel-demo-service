package com.hotel.demo.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {
    
    private GlobalExceptionHandler handler;
    
    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }
    
    @Test
    void testHandleInvalidEmail() {
        InvalidEmailException exception = new InvalidEmailException("Invalid email: test");
        
        ProblemDetail problem = handler.handleInvalidEmail(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertEquals("Invalid email: test", problem.getDetail());
        assertEquals("Invalid Email Format", problem.getTitle());
        assertEquals("INVALID_EMAIL_FORMAT", problem.getProperties().get("errorCode"));
    }
    
    @Test
    void testHandleDatabaseConnection() {
        DatabaseConnectionException exception = new DatabaseConnectionException(
            "Connection failed", 
            new RuntimeException("Timeout")
        );
        
        ProblemDetail problem = handler.handleDatabaseConnection(exception);
        
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), problem.getStatus());
        assertTrue(problem.getDetail().contains("Unable to connect to the database"));
        assertEquals("Database Connection Error", problem.getTitle());
        assertEquals("DATABASE_CONNECTION_ERROR", problem.getProperties().get("errorCode"));
    }
    
    @Test
    void testHandleSearchTimeout() {
        SearchTimeoutException exception = new SearchTimeoutException("Search took too long");
        
        ProblemDetail problem = handler.handleSearchTimeout(exception);
        
        assertEquals(HttpStatus.GATEWAY_TIMEOUT.value(), problem.getStatus());
        assertEquals("Search took too long", problem.getDetail());
        assertEquals("Search Timeout", problem.getTitle());
        assertEquals("SEARCH_TIMEOUT", problem.getProperties().get("errorCode"));
    }
    
    @Test
    void testHandleValidation() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("searchRequest", "email", "must not be empty");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        ProblemDetail problem = handler.handleValidation(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertNotNull(problem.getDetail());
        assertTrue(problem.getDetail().contains("email"));
        assertTrue(problem.getDetail().contains("must not be empty"));
        assertEquals("Validation Error", problem.getTitle());
        assertNotNull(problem.getProperties());
        assertEquals("VALIDATION_ERROR", problem.getProperties().get("errorCode"));
    }
    
    @Test
    void testHandleValidation_multipleErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("searchRequest", "email", "must not be empty");
        FieldError error2 = new FieldError("searchRequest", "customerId", "must not be null");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        ProblemDetail problem = handler.handleValidation(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertNotNull(problem.getDetail());
        assertTrue(problem.getDetail().contains("email"));
        assertTrue(problem.getDetail().contains("customerId"));
    }
    
    @Test
    void testHandleValidation_noErrors() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(List.of());
        
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        
        ProblemDetail problem = handler.handleValidation(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertEquals("Validation failed", problem.getDetail());
    }
    
    @Test
    void testHandleConstraintViolation() {
        ConstraintViolationException exception = new ConstraintViolationException(
            "Constraint violated",
            new HashSet<>()
        );
        
        ProblemDetail problem = handler.handleConstraintViolation(exception);
        
        assertEquals(HttpStatus.BAD_REQUEST.value(), problem.getStatus());
        assertEquals("Constraint violated", problem.getDetail());
        assertEquals("Constraint Violation", problem.getTitle());
        assertEquals("CONSTRAINT_VIOLATION", problem.getProperties().get("errorCode"));
    }
    
    @Test
    void testHandleGeneral() {
        Exception exception = new RuntimeException("Unexpected error");
        
        ProblemDetail problem = handler.handleGeneral(exception);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), problem.getStatus());
        assertTrue(problem.getDetail().contains("unexpected error"));
        assertEquals("Internal Server Error", problem.getTitle());
        assertEquals("INTERNAL_SERVER_ERROR", problem.getProperties().get("errorCode"));
    }
}
