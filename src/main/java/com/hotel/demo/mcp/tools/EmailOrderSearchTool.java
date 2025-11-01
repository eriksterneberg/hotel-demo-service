package com.hotel.demo.mcp.tools;

import com.hotel.demo.model.dto.SearchRequest;
import com.hotel.demo.model.dto.SearchResponse;
import com.hotel.demo.service.OrderSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

/**
 * MCP Tool for searching hotel booking orders by email address.
 * This tool exposes order search functionality to AI assistants via the Model Context Protocol.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailOrderSearchTool {
    
    private final OrderSearchService orderSearchService;
    
    /**
     * Search hotel booking orders by customer email address.
     * 
     * @param email The customer email address to search for
     * @param customerId The customer ID for session tracking
     * @param minConfidenceThreshold Minimum confidence score (0-100, default 70)
     * @return Search response with results and metadata
     */
    @McpTool(name = "search_orders_by_email",
             description = "Search hotel booking orders by customer email address. " +
                           "Retrieves comprehensive order details including booking dates, " +
                           "hotel information, guest names, and payment details.")
    public SearchResponse searchOrdersByEmail(
            @McpToolParam(description = "The customer email address", required = true) String email,
            @McpToolParam(description = "The customer ID for tracking", required = true) String customerId,
            @McpToolParam(description = "Minimum confidence score (0-100)", required = false) Integer minConfidenceThreshold) {
        
        log.info("MCP Tool 'search_orders_by_email' invoked by customer: {}", customerId);
        log.debug("Searching for email: {} with confidence threshold: {}%", 
                  email, minConfidenceThreshold != null ? minConfidenceThreshold : 70);
        
        // Create search request with provided parameters
        SearchRequest searchRequest = new SearchRequest(
            email,
            customerId,
            minConfidenceThreshold != null ? minConfidenceThreshold : 70
        );
        
        // Execute search
        SearchResponse searchResponse = orderSearchService.searchByEmail(searchRequest);
        
        log.info("MCP Tool 'search_orders_by_email' completed: {} results returned", 
                 searchResponse.results().size());
        
        return searchResponse;
    }
}
