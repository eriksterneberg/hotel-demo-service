package com.hotel.demo.exception;

/**
 * Base exception for order search operations.
 */
public class OrderSearchException extends RuntimeException {
    
    public OrderSearchException(String message) {
        super(message);
    }
    
    public OrderSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
