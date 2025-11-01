package com.hotel.demo.model.entity;

import jakarta.validation.constraints.*;
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
    
    // Default constructor
    public HotelBookingOrder() {
    }
    
    // All-args constructor
    public HotelBookingOrder(UUID orderId, String customerEmail, String customerEmailEncrypted,
                            LocalDateTime bookingDateStart, LocalDateTime bookingDateEnd,
                            String hotelName, String hotelAddress, String roomType,
                            List<String> guestNames, String paymentMethod, BigDecimal totalAmount,
                            OrderStatus orderStatus, Instant createdAt) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.customerEmailEncrypted = customerEmailEncrypted;
        this.bookingDateStart = bookingDateStart;
        this.bookingDateEnd = bookingDateEnd;
        this.hotelName = hotelName;
        this.hotelAddress = hotelAddress;
        this.roomType = roomType;
        this.guestNames = guestNames;
        this.paymentMethod = paymentMethod;
        this.totalAmount = totalAmount;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
    }
    
    // Validation method
    @AssertTrue(message = "Check-out date must be after check-in date")
    public boolean isValidDateRange() {
        if (bookingDateStart == null || bookingDateEnd == null) {
            return true; // Let @NotNull handle null validation
        }
        return bookingDateEnd.isAfter(bookingDateStart);
    }
    
    // Getters and Setters
    public UUID getOrderId() {
        return orderId;
    }
    
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }
    
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    
    public String getCustomerEmailEncrypted() {
        return customerEmailEncrypted;
    }
    
    public void setCustomerEmailEncrypted(String customerEmailEncrypted) {
        this.customerEmailEncrypted = customerEmailEncrypted;
    }
    
    public LocalDateTime getBookingDateStart() {
        return bookingDateStart;
    }
    
    public void setBookingDateStart(LocalDateTime bookingDateStart) {
        this.bookingDateStart = bookingDateStart;
    }
    
    public LocalDateTime getBookingDateEnd() {
        return bookingDateEnd;
    }
    
    public void setBookingDateEnd(LocalDateTime bookingDateEnd) {
        this.bookingDateEnd = bookingDateEnd;
    }
    
    public String getHotelName() {
        return hotelName;
    }
    
    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }
    
    public String getHotelAddress() {
        return hotelAddress;
    }
    
    public void setHotelAddress(String hotelAddress) {
        this.hotelAddress = hotelAddress;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public List<String> getGuestNames() {
        return guestNames;
    }
    
    public void setGuestNames(List<String> guestNames) {
        this.guestNames = guestNames;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
    
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
