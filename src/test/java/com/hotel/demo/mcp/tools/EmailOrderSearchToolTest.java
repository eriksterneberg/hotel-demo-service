package com.hotel.demo.mcp.tools;

import com.hotel.demo.model.dto.SearchMetadata;
import com.hotel.demo.model.dto.SearchRequest;
import com.hotel.demo.model.dto.SearchResponse;
import com.hotel.demo.model.dto.SearchResult;
import com.hotel.demo.model.entity.OrderStatus;
import com.hotel.demo.service.OrderSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmailOrderSearchTool}.
 */
@ExtendWith(MockitoExtension.class)
class EmailOrderSearchToolTest {

    @Mock
    private OrderSearchService orderSearchService;

    @InjectMocks
    private EmailOrderSearchTool emailOrderSearchTool;

    private SearchResult testResult;
    private SearchResponse testResponse;

    @BeforeEach
    void setUp() {
        testResult = new SearchResult(
            UUID.randomUUID(),
            "test@example.com",
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(2),
            "Test Hotel",
            "123 Main St, New York",
            "Deluxe Suite",
            List.of("John Doe"),
            "Credit Card",
            BigDecimal.valueOf(299.99),
            OrderStatus.CONFIRMED,
            100
        );

        testResponse = new SearchResponse(
            List.of(testResult),
            new SearchMetadata(
                "test@example.com",
                1,
                70,
                50L,
                1,
                0
            )
        );
    }

    @Test
    void searchOrdersByEmail_ShouldReturnResults() {
        // Given
        String email = "test@example.com";
        String customerId = "customer123";
        
        when(orderSearchService.searchByEmail(any(SearchRequest.class)))
            .thenReturn(testResponse);

        // When
        SearchResponse response = emailOrderSearchTool.searchOrdersByEmail(email, customerId, 70);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.results()).hasSize(1);
        assertThat(response.results().get(0).customerEmail()).isEqualTo(email);
        assertThat(response.searchMetadata().resultCount()).isEqualTo(1);
        
        verify(orderSearchService, times(1)).searchByEmail(any(SearchRequest.class));
    }

    @Test
    void searchOrdersByEmail_WithNullThreshold_ShouldUseDefault70() {
        // Given
        String email = "test@example.com";
        String customerId = "customer123";
        
        when(orderSearchService.searchByEmail(any(SearchRequest.class)))
            .thenReturn(testResponse);

        // When
        SearchResponse response = emailOrderSearchTool.searchOrdersByEmail(email, customerId, null);

        // Then
        assertThat(response).isNotNull();
        
        verify(orderSearchService, times(1)).searchByEmail(argThat(request ->
            request.minConfidenceThreshold() == 70
        ));
    }

    @Test
    void searchOrdersByEmail_ShouldLogInvocation() {
        // Given
        String email = "test@example.com";
        String customerId = "customer123";
        
        when(orderSearchService.searchByEmail(any(SearchRequest.class)))
            .thenReturn(testResponse);

        // When
        emailOrderSearchTool.searchOrdersByEmail(email, customerId, 80);

        // Then
        verify(orderSearchService, times(1)).searchByEmail(argThat(request ->
            request.email().equals(email) &&
            request.customerId().equals(customerId) &&
            request.minConfidenceThreshold() == 80
        ));
    }
}
