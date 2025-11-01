package com.hotel.demo.model.dto;

import jakarta.validation.constraints.*;

/**
 * Search request DTO for order search operations.
 * 
 * @param email Email address to search for
 * @param customerId Post-sales staff customer ID for authentication
 * @param minConfidenceThreshold Minimum confidence score (50-100%)
 */
public record SearchRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    String email,
    
    @NotBlank(message = "Customer ID is required for authentication")
    @Size(min = 1, max = 50, message = "Customer ID must be 1-50 characters")
    String customerId,
    
    @Min(value = 50, message = "Minimum confidence threshold is 50%")
    @Max(value = 100, message = "Maximum confidence threshold is 100%")
    Integer minConfidenceThreshold
) {
    /**
     * Constructor with defaults for optional parameters.
     */
    public SearchRequest(String email, String customerId) {
        this(email, customerId, 50);
    }
    
    /**
     * Canonical constructor with validation.
     */
    public SearchRequest {
        if (minConfidenceThreshold == null) {
            minConfidenceThreshold = 50;
        }
    }
}
