package com.hotel.demo.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for exception classes.
 */
class ExceptionTests {
    
    @Test
    void testInvalidEmailException() {
        String message = "Invalid email format";
        InvalidEmailException exception = new InvalidEmailException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(OrderSearchException.class, exception);
    }
    
    @Test
    void testDatabaseConnectionException() {
        String message = "Database connection failed";
        Throwable cause = new RuntimeException("Connection timeout");
        DatabaseConnectionException exception = new DatabaseConnectionException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertInstanceOf(OrderSearchException.class, exception);
    }
    
    @Test
    void testSearchTimeoutException() {
        String message = "Search operation timed out";
        SearchTimeoutException exception = new SearchTimeoutException(message);
        
        assertEquals(message, exception.getMessage());
        assertInstanceOf(OrderSearchException.class, exception);
    }
    
    @Test
    void testOrderSearchException_withMessage() {
        String message = "Order search failed";
        OrderSearchException exception = new OrderSearchException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    void testOrderSearchException_withMessageAndCause() {
        String message = "Order search failed";
        Throwable cause = new RuntimeException("Root cause");
        OrderSearchException exception = new OrderSearchException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
