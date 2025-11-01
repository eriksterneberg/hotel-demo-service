package com.hotel.demo.model.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for entity classes.
 */
class EntityTests {
    
    @Test
    void testHotelBookingOrder_constructor() {
        UUID orderId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.now().plusDays(1);
        LocalDateTime checkOut = LocalDateTime.now().plusDays(3);
        
        HotelBookingOrder order = new HotelBookingOrder(
            orderId,
            "test@example.com",
            "ENC(test@example.com)",
            checkIn,
            checkOut,
            "Test Hotel",
            "123 Test St",
            "Deluxe Suite",
            List.of("Guest 1", "Guest 2"),
            "Credit Card",
            new BigDecimal("299.99"),
            OrderStatus.CONFIRMED,
            Instant.now()
        );
        
        assertEquals(orderId, order.getOrderId());
        assertEquals("test@example.com", order.getCustomerEmail());
        assertEquals("Test Hotel", order.getHotelName());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        assertTrue(order.isValidDateRange());
    }
    
    @Test
    void testHotelBookingOrder_defaultConstructor() {
        HotelBookingOrder order = new HotelBookingOrder();
        
        assertNull(order.getOrderId());
        assertNull(order.getCustomerEmail());
    }
    
    @Test
    void testHotelBookingOrder_settersAndGetters() {
        HotelBookingOrder order = new HotelBookingOrder();
        UUID orderId = UUID.randomUUID();
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(2);
        BigDecimal amount = new BigDecimal("499.99");
        Instant created = Instant.now();
        
        order.setOrderId(orderId);
        order.setCustomerEmail("guest@example.com");
        order.setCustomerEmailEncrypted("ENC(guest@example.com)");
        order.setBookingDateStart(checkIn);
        order.setBookingDateEnd(checkOut);
        order.setHotelName("Beach Resort");
        order.setHotelAddress("456 Beach Rd");
        order.setRoomType("Ocean View");
        order.setGuestNames(List.of("Guest"));
        order.setPaymentMethod("PayPal");
        order.setTotalAmount(amount);
        order.setOrderStatus(OrderStatus.PENDING);
        order.setCreatedAt(created);
        
        assertEquals(orderId, order.getOrderId());
        assertEquals("guest@example.com", order.getCustomerEmail());
        assertEquals("ENC(guest@example.com)", order.getCustomerEmailEncrypted());
        assertEquals(checkIn, order.getBookingDateStart());
        assertEquals(checkOut, order.getBookingDateEnd());
        assertEquals("Beach Resort", order.getHotelName());
        assertEquals("456 Beach Rd", order.getHotelAddress());
        assertEquals("Ocean View", order.getRoomType());
        assertEquals(1, order.getGuestNames().size());
        assertEquals("PayPal", order.getPaymentMethod());
        assertEquals(amount, order.getTotalAmount());
        assertEquals(OrderStatus.PENDING, order.getOrderStatus());
        assertEquals(created, order.getCreatedAt());
    }
    
    @Test
    void testHotelBookingOrder_validDateRange() {
        HotelBookingOrder order = new HotelBookingOrder();
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.plusDays(2);
        
        order.setBookingDateStart(checkIn);
        order.setBookingDateEnd(checkOut);
        
        assertTrue(order.isValidDateRange());
    }
    
    @Test
    void testHotelBookingOrder_invalidDateRange() {
        HotelBookingOrder order = new HotelBookingOrder();
        LocalDateTime checkIn = LocalDateTime.now();
        LocalDateTime checkOut = checkIn.minusDays(1);
        
        order.setBookingDateStart(checkIn);
        order.setBookingDateEnd(checkOut);
        
        assertFalse(order.isValidDateRange());
    }
    
    @Test
    void testHotelBookingOrder_sameDateRange() {
        HotelBookingOrder order = new HotelBookingOrder();
        LocalDateTime checkIn = LocalDateTime.now();
        
        order.setBookingDateStart(checkIn);
        order.setBookingDateEnd(checkIn);
        
        assertFalse(order.isValidDateRange());
    }
    
    @Test
    void testHotelBookingOrder_nullDates() {
        HotelBookingOrder order = new HotelBookingOrder();
        
        assertTrue(order.isValidDateRange()); // Null dates should return true
    }
    
    @Test
    void testOrderStatus_values() {
        assertEquals("PENDING", OrderStatus.PENDING.name());
        assertEquals("CONFIRMED", OrderStatus.CONFIRMED.name());
        assertEquals("CHECKED_IN", OrderStatus.CHECKED_IN.name());
        assertEquals("CHECKED_OUT", OrderStatus.CHECKED_OUT.name());
        assertEquals("CANCELLED", OrderStatus.CANCELLED.name());
    }
    
    @Test
    void testOrderStatus_valueOf() {
        assertEquals(OrderStatus.PENDING, OrderStatus.valueOf("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.valueOf("CONFIRMED"));
        assertEquals(OrderStatus.CHECKED_IN, OrderStatus.valueOf("CHECKED_IN"));
        assertEquals(OrderStatus.CHECKED_OUT, OrderStatus.valueOf("CHECKED_OUT"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.valueOf("CANCELLED"));
    }
    
    @Test
    void testCustomerSession_newSession() {
        CustomerSession session = CustomerSession.newSession("customer123");
        
        assertNotNull(session.sessionId());
        assertEquals("customer123", session.customerId());
        assertNotNull(session.sessionStart());
        assertNull(session.sessionEnd());
        assertEquals(0, session.searchCount());
        assertNotNull(session.searchedEmails());
        assertTrue(session.searchedEmails().isEmpty());
    }
    
    @Test
    void testCustomerSession_withSearch() {
        CustomerSession session = CustomerSession.newSession("customer123");
        String email = "test@example.com";
        
        CustomerSession updated = session.withSearch(email);
        
        assertEquals(session.sessionId(), updated.sessionId());
        assertEquals(session.customerId(), updated.customerId());
        assertEquals(1, updated.searchCount());
        assertEquals(1, updated.searchedEmails().size());
        assertTrue(updated.searchedEmails().contains(email));
    }
    
    @Test
    void testCustomerSession_multipleSearches() {
        CustomerSession session = CustomerSession.newSession("customer123");
        
        CustomerSession session1 = session.withSearch("first@example.com");
        CustomerSession session2 = session1.withSearch("second@example.com");
        
        assertEquals(2, session2.searchCount());
        assertEquals(2, session2.searchedEmails().size());
        assertTrue(session2.searchedEmails().contains("first@example.com"));
        assertTrue(session2.searchedEmails().contains("second@example.com"));
    }
    
    @Test
    void testCustomerSession_end() {
        CustomerSession session = CustomerSession.newSession("customer123");
        
        CustomerSession ended = session.end();
        
        assertEquals(session.sessionId(), ended.sessionId());
        assertEquals(session.customerId(), ended.customerId());
        assertEquals(session.sessionStart(), ended.sessionStart());
        assertNotNull(ended.sessionEnd());
        assertEquals(0, ended.searchCount());
    }
    
    @Test
    void testCustomerSession_recordConstructor() {
        String sessionId = UUID.randomUUID().toString();
        String customerId = "cust456";
        Instant start = Instant.now();
        Instant end = Instant.now().plusSeconds(3600);
        
        CustomerSession session = new CustomerSession(
            sessionId,
            customerId,
            start,
            end,
            5,
            List.of("email1@example.com", "email2@example.com")
        );
        
        assertEquals(sessionId, session.sessionId());
        assertEquals(customerId, session.customerId());
        assertEquals(start, session.sessionStart());
        assertEquals(end, session.sessionEnd());
        assertEquals(5, session.searchCount());
        assertEquals(2, session.searchedEmails().size());
    }
}
