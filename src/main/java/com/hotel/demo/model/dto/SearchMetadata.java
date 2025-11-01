package com.hotel.demo.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Search metadata containing information about the search operation.
 * 
 * @param searchEmail The email address that was searched for
 * @param resultCount Number of results returned
 * @param minConfidenceThreshold Confidence threshold applied
 * @param executionTimeMs Search execution time in milliseconds
 * @param exactMatchCount Number of exact matches (confidence = 100%)
 * @param fuzzyMatchCount Number of fuzzy matches (confidence 50-99%)
 */
public record SearchMetadata(
    @NotBlank
    String searchEmail,
    
    @NotNull
    @Min(0)
    Integer resultCount,
    
    @NotNull
    @Min(50)
    Integer minConfidenceThreshold,
    
    @NotNull
    @Min(0)
    Long executionTimeMs,
    
    @NotNull
    @Min(0)
    Integer exactMatchCount,
    
    @NotNull
    @Min(0)
    Integer fuzzyMatchCount
) {
}
