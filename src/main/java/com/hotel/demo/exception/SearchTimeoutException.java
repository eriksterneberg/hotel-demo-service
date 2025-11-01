package com.hotel.demo.exception;

/**
 * Exception thrown when search operation times out.
 */
public class SearchTimeoutException extends OrderSearchException {
    
    public SearchTimeoutException(String message) {
        super(message);
    }
}
