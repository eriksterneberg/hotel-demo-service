package com.hotel.demo.model.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Search response containing results and metadata.
 * 
 * @param results List of matching orders with confidence scores
 * @param searchMetadata Metadata about the search operation
 */
public record SearchResponse(
    @NotNull
    List<SearchResult> results,
    
    @NotNull
    SearchMetadata searchMetadata
) {
}
