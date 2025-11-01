package com.hotel.demo.model.dto;

import com.hotel.demo.model.entity.OrderStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Search result DTO representing a matched order with confidence score.
 * 
 * @param orderId Unique order identifier
 * @param customerEmail Customer email address
 * @param bookingDateStart Check-in date and time
 * @param bookingDateEnd Check-out date and time
 * @param hotelName Name of the hotel
 * @param hotelAddress Full address of the hotel
 * @param roomType Type of room booked
 * @param guestNames Names of all guests
 * @param paymentMethod Payment method used
 * @param totalAmount Total booking amount
 * @param orderStatus Current order status
 * @param confidenceScore Match confidence score (0-100%)
 */
public record SearchResult(
    @NotNull
    UUID orderId,
    
    @NotBlank
    @Email
    String customerEmail,
    
    @NotNull
    LocalDateTime bookingDateStart,
    
    @NotNull
    LocalDateTime bookingDateEnd,
    
    @NotBlank
    String hotelName,
    
    @NotBlank
    String hotelAddress,
    
    @NotBlank
    String roomType,
    
    @NotEmpty
    List<String> guestNames,
    
    @NotBlank
    String paymentMethod,
    
    @NotNull
    @DecimalMin("0.0")
    BigDecimal totalAmount,
    
    @NotNull
    OrderStatus orderStatus,
    
    @NotNull
    @Min(0) @Max(100)
    Integer confidenceScore
) {
    /**
     * Calculate booking duration in days.
     */
    public long bookingDurationDays() {
        return ChronoUnit.DAYS.between(bookingDateStart, bookingDateEnd);
    }
    
    /**
     * Check if this is an exact match.
     */
    public boolean isExactMatch() {
        return confidenceScore == 100;
    }
    
    /**
     * Check if this is a fuzzy match.
     */
    public boolean isFuzzyMatch() {
        return confidenceScore >= 50 && confidenceScore < 100;
    }
}
