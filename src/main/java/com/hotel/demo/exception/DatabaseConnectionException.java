package com.hotel.demo.exception;

/**
 * Exception thrown when database connection fails.
 */
public class DatabaseConnectionException extends OrderSearchException {
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
