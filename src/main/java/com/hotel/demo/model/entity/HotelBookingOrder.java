package com.hotel.demo.model.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Hotel booking order entity representing a customer's hotel reservation.
 * Stored in Cassandra with PII fields encrypted at rest.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("booking_orders")
public class HotelBookingOrder {
    
    @PrimaryKey
    @NotNull
    private UUID orderId;
    
    @Column("customer_email")
    @NotBlank(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String customerEmail;
    
    @Column("customer_email_encrypted")
    @NotBlank
    private String customerEmailEncrypted;
    
    @Column("booking_date_start")
    @NotNull(message = "Check-in date is required")
    private LocalDateTime bookingDateStart;
    
    @Column("booking_date_end")
    @NotNull(message = "Check-out date is required")
    private LocalDateTime bookingDateEnd;
    
    @Column("hotel_name")
    @NotBlank(message = "Hotel name is required")
    @Size(max = 200, message = "Hotel name must not exceed 200 characters")
    private String hotelName;
    
    @Column("hotel_address")
    @NotBlank(message = "Hotel address is required")
    @Size(max = 500, message = "Hotel address must not exceed 500 characters")
    private String hotelAddress;
    
    @Column("room_type")
    @NotBlank(message = "Room type is required")
    @Size(max = 100, message = "Room type must not exceed 100 characters")
    private String roomType;
    
    @Column("guest_names")
    @NotEmpty(message = "At least one guest name is required")
    @Size(max = 10, message = "Maximum 10 guests per booking")
    private List<String> guestNames;
    
    @Column("payment_method")
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    @Column("total_amount")
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be positive")
    @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
    private BigDecimal totalAmount;
    
    @Column("order_status")
    @NotNull
    private OrderStatus orderStatus;
    
    @Column("created_at")
    @NotNull
    private Instant createdAt;
    
    // Validation method
    @AssertTrue(message = "Check-out date must be after check-in date")
    public boolean isValidDateRange() {
        if (bookingDateStart == null || bookingDateEnd == null) {
            return true; // Let @NotNull handle null validation
        }
        return bookingDateEnd.isAfter(bookingDateStart);
    }
}
