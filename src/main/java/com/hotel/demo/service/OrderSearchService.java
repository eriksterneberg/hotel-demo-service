package com.hotel.demo.service;

import com.hotel.demo.exception.DatabaseConnectionException;
import com.hotel.demo.exception.InvalidEmailException;
import com.hotel.demo.model.dto.SearchMetadata;
import com.hotel.demo.model.dto.SearchRequest;
import com.hotel.demo.model.dto.SearchResponse;
import com.hotel.demo.model.dto.SearchResult;
import com.hotel.demo.model.entity.HotelBookingOrder;
import com.hotel.demo.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service for searching hotel booking orders by customer email.
 * Supports exact email matching with 100% confidence scores.
 */
@Service
@Validated
public class OrderSearchService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderSearchService.class);
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private final OrderRepository orderRepository;
    
    public OrderSearchService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    /**
     * Search for orders by customer email (exact match).
     * 
     * @param request Search request with email and customer ID
     * @return Search response with results and metadata
     */
    public SearchResponse searchByEmail(SearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        log.debug("Searching orders for email (masked): ***@{}", 
                  extractDomain(request.email()));
        
        try {
            // Validate email format
            validateEmail(request.email());
            
            // Perform exact match search
            List<HotelBookingOrder> orders = orderRepository.findByCustomerEmail(request.email());
            
            // Convert to search results with 100% confidence
            List<SearchResult> results = orders.stream()
                .map(order -> toSearchResult(order, 100))
                .sorted(Comparator.comparing(SearchResult::confidenceScore).reversed())
                .filter(result -> result.confidenceScore() >= request.minConfidenceThreshold())
                .toList();
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Count exact matches
            long exactCount = results.stream()
                .filter(SearchResult::isExactMatch)
                .count();
            
            SearchMetadata metadata = new SearchMetadata(
                maskEmail(request.email()),
                results.size(),
                request.minConfidenceThreshold(),
                executionTime,
                (int) exactCount,
                0 // No fuzzy matches in MVP
            );
            
            log.info("Search completed: {} results found in {}ms", 
                     results.size(), executionTime);
            
            return new SearchResponse(results, metadata);
            
        } catch (InvalidEmailException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error during search", e);
            throw new DatabaseConnectionException("Failed to query database", e);
        } catch (Exception e) {
            log.error("Unexpected error during search", e);
            throw new RuntimeException("Search failed", e);
        }
    }
    
    /**
     * Validate email format.
     */
    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailException("Invalid email format: " + maskEmail(email));
        }
    }
    
    /**
     * Convert HotelBookingOrder to SearchResult with confidence score.
     */
    private SearchResult toSearchResult(HotelBookingOrder order, int confidenceScore) {
        return new SearchResult(
            order.getOrderId(),
            order.getCustomerEmail(),
            order.getBookingDateStart(),
            order.getBookingDateEnd(),
            order.getHotelName(),
            order.getHotelAddress(),
            order.getRoomType(),
            order.getGuestNames(),
            order.getPaymentMethod(),
            order.getTotalAmount(),
            order.getOrderStatus(),
            confidenceScore
        );
    }
    
    /**
     * Mask email address for logging (PII protection).
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        return "***" + email.substring(email.indexOf('@'));
    }
    
    /**
     * Extract domain from email for logging.
     */
    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) {
            return "unknown";
        }
        return email.substring(email.indexOf('@') + 1);
    }
}
