package com.hotel.demo.service;

import com.hotel.demo.exception.InvalidEmailException;
import com.hotel.demo.model.dto.SearchRequest;
import com.hotel.demo.model.dto.SearchResponse;
import com.hotel.demo.model.entity.HotelBookingOrder;
import com.hotel.demo.model.entity.OrderStatus;
import com.hotel.demo.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderSearchService.
 */
@ExtendWith(MockitoExtension.class)
class OrderSearchServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    private OrderSearchService orderSearchService;
    
    @BeforeEach
    void setUp() {
        orderSearchService = new OrderSearchService(orderRepository);
    }
    
    @Test
    void testSearchByEmail_exactMatch_returnsResults() {
        // Given
        String email = "john.doe@example.com";
        SearchRequest request = new SearchRequest(email, "staff-123", 50);
        
        HotelBookingOrder order = createTestOrder(email);
        when(orderRepository.findByCustomerEmail(email)).thenReturn(List.of(order));
        
        // When
        SearchResponse response = orderSearchService.searchByEmail(request);
        
        // Then
        assertNotNull(response);
        assertEquals(1, response.results().size());
        assertEquals(100, response.results().get(0).confidenceScore());
        assertEquals(email, response.results().get(0).customerEmail());
        verify(orderRepository, times(1)).findByCustomerEmail(email);
    }
    
    @Test
    void testSearchByEmail_noResults_returnsEmptyList() {
        // Given
        String email = "nonexistent@example.com";
        SearchRequest request = new SearchRequest(email, "staff-123", 50);
        
        when(orderRepository.findByCustomerEmail(email)).thenReturn(List.of());
        
        // When
        SearchResponse response = orderSearchService.searchByEmail(request);
        
        // Then
        assertNotNull(response);
        assertTrue(response.results().isEmpty());
        assertEquals(0, response.searchMetadata().resultCount());
    }
    
    @Test
    void testSearchByEmail_invalidEmail_throwsException() {
        // Given
        SearchRequest request = new SearchRequest("invalid-email", "staff-123", 50);
        
        // When/Then
        assertThrows(InvalidEmailException.class, 
            () -> orderSearchService.searchByEmail(request));
        
        verify(orderRepository, never()).findByCustomerEmail(anyString());
    }
    
    @Test
    void testSearchByEmail_multipleResults_sortedByConfidence() {
        // Given
        String email = "frequent@example.com";
        SearchRequest request = new SearchRequest(email, "staff-123", 50);
        
        HotelBookingOrder order1 = createTestOrder(email);
        HotelBookingOrder order2 = createTestOrder(email);
        when(orderRepository.findByCustomerEmail(email)).thenReturn(List.of(order1, order2));
        
        // When
        SearchResponse response = orderSearchService.searchByEmail(request);
        
        // Then
        assertNotNull(response);
        assertEquals(2, response.results().size());
        assertEquals(100, response.results().get(0).confidenceScore());
        assertEquals(100, response.results().get(1).confidenceScore());
    }
    
    @Test
    void testSearchByEmail_belowThreshold_filtered() {
        // Given
        String email = "test@example.com";
        SearchRequest request = new SearchRequest(email, "staff-123", 100);
        
        when(orderRepository.findByCustomerEmail(email)).thenReturn(List.of());
        
        // When
        SearchResponse response = orderSearchService.searchByEmail(request);
        
        // Then
        assertNotNull(response);
        assertEquals(0, response.results().size());
    }
    
    private HotelBookingOrder createTestOrder(String email) {
        return new HotelBookingOrder(
            UUID.randomUUID(),
            email,
            "ENC(encrypted_" + email + ")",
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            "Test Hotel",
            "123 Test St, City, Country",
            "Standard Room",
            List.of("Test Guest"),
            "Credit Card",
            new BigDecimal("299.99"),
            OrderStatus.CONFIRMED,
            Instant.now()
        );
    }
}
