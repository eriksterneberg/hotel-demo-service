package com.hotel.demo.model.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Customer session tracking for audit and session management.
 * 
 * @param sessionId Unique session identifier
 * @param customerId Post-sales staff customer ID
 * @param sessionStart Session start timestamp
 * @param sessionEnd Session end timestamp (null if active)
 * @param searchCount Number of searches performed in this session
 * @param searchedEmails List of email addresses searched (for audit)
 */
public record CustomerSession(
    @NotBlank
    String sessionId,
    
    @NotBlank
    String customerId,
    
    @NotNull
    Instant sessionStart,
    
    Instant sessionEnd,
    
    @Min(0)
    Integer searchCount,
    
    @NotNull
    List<String> searchedEmails
) {
    /**
     * Create a new session for the given customer ID.
     */
    public static CustomerSession newSession(String customerId) {
        return new CustomerSession(
            UUID.randomUUID().toString(),
            customerId,
            Instant.now(),
            null,
            0,
            new ArrayList<>()
        );
    }
    
    /**
     * Add a search to this session.
     */
    public CustomerSession withSearch(String email) {
        List<String> updatedSearches = new ArrayList<>(searchedEmails);
        updatedSearches.add(email);
        return new CustomerSession(
            sessionId,
            customerId,
            sessionStart,
            sessionEnd,
            searchCount + 1,
            updatedSearches
        );
    }
    
    /**
     * End this session.
     */
    public CustomerSession end() {
        return new CustomerSession(
            sessionId,
            customerId,
            sessionStart,
            Instant.now(),
            searchCount,
            searchedEmails
        );
    }
}
