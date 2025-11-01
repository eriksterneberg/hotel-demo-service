package com.hotel.demo.model.dto;

import com.hotel.demo.model.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for DTO classes.
 */
class DtoTests {
    
    @Test
    void testSearchResult_constructor() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(3);
        
        SearchResult result = new SearchResult(
            orderId,
            "test@example.com",
            checkIn,
            checkOut,
            "Test Hotel",
            "123 Test St",
            "Deluxe Suite",
            List.of("Guest 1"),
            "Credit Card",
            new BigDecimal("299.99"),
            OrderStatus.CONFIRMED,
            100
        );
        
        assertEquals(orderId, result.orderId());
        assertEquals("test@example.com", result.customerEmail());
        assertEquals(100, result.confidenceScore());
        assertTrue(result.isExactMatch());
        assertFalse(result.isFuzzyMatch());
    }
    
    @Test
    void testSearchResult_bookingDuration() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.of(2025, 12, 1, 14, 0);
        LocalDateTime checkOut = LocalDateTime.of(2025, 12, 4, 14, 0);
        
        SearchResult result = new SearchResult(
            orderId,
            "test@example.com",
            checkIn,
            checkOut,
            "Test Hotel",
            "123 Test St",
            "Standard Room",
            List.of("Guest"),
            "Credit Card",
            new BigDecimal("199.99"),
            OrderStatus.CONFIRMED,
            100
        );
        
        assertEquals(3, result.bookingDurationDays());
    }
    
    @Test
    void testSearchResult_exactMatch() {
        SearchResult result = createSearchResult(100);
        
        assertTrue(result.isExactMatch());
        assertFalse(result.isFuzzyMatch());
    }
    
    @Test
    void testSearchResult_fuzzyMatch_high() {
        SearchResult result = createSearchResult(90);
        
        assertFalse(result.isExactMatch());
        assertTrue(result.isFuzzyMatch());
    }
    
    @Test
    void testSearchResult_fuzzyMatch_low() {
        SearchResult result = createSearchResult(50);
        
        assertFalse(result.isExactMatch());
        assertTrue(result.isFuzzyMatch());
    }
    
    @Test
    void testSearchResult_belowFuzzyThreshold() {
        SearchResult result = createSearchResult(49);
        
        assertFalse(result.isExactMatch());
        assertFalse(result.isFuzzyMatch());
    }
    
    @Test
    void testSearchRequest_constructor() {
        SearchRequest request = new SearchRequest(
            "test@example.com",
            "staff-123",
            70
        );
        
        assertEquals("test@example.com", request.email());
        assertEquals("staff-123", request.customerId());
        assertEquals(70, request.minConfidenceThreshold());
    }
    
    @Test
    void testSearchMetadata_constructor() {
        SearchMetadata metadata = new SearchMetadata(
            "***@example.com",
            5,
            70,
            125L,
            5,
            0
        );
        
        assertEquals("***@example.com", metadata.searchEmail());
        assertEquals(5, metadata.resultCount());
        assertEquals(70, metadata.minConfidenceThreshold());
        assertEquals(125L, metadata.executionTimeMs());
        assertEquals(5, metadata.exactMatchCount());
        assertEquals(0, metadata.fuzzyMatchCount());
    }
    
    @Test
    void testSearchResponse_constructor() {
        SearchResult result = createSearchResult(100);
        SearchMetadata metadata = new SearchMetadata(
            "***@example.com",
            1,
            50,
            50L,
            1,
            0
        );
        
        SearchResponse response = new SearchResponse(
            List.of(result),
            metadata
        );
        
        assertEquals(1, response.results().size());
        assertEquals(result, response.results().get(0));
        assertEquals(metadata, response.searchMetadata());
    }
    
    @Test
    void testSearchResponse_emptyResults() {
        SearchMetadata metadata = new SearchMetadata(
            "***@example.com",
            0,
            50,
            25L,
            0,
            0
        );
        
        SearchResponse response = new SearchResponse(
            List.of(),
            metadata
        );
        
        assertTrue(response.results().isEmpty());
        assertEquals(0, response.searchMetadata().resultCount());
    }
    
    private SearchResult createSearchResult(int confidenceScore) {
        return new SearchResult(
            UUID.randomUUID(),
            "test@example.com",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            "Hotel",
            "Address",
            "Room",
            List.of("Guest"),
            "Credit Card",
            new BigDecimal("100.00"),
            OrderStatus.CONFIRMED,
            confidenceScore
        );
    }
}
