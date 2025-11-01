package com.hotel.demo.exception;

/**
 * Exception thrown when email format is invalid.
 */
public class InvalidEmailException extends OrderSearchException {
    
    public InvalidEmailException(String message) {
        super(message);
    }
}
