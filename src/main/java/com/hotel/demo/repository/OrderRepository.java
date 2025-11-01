package com.hotel.demo.repository;

import com.hotel.demo.model.entity.HotelBookingOrder;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for hotel booking order data access.
 * Uses Spring Data Cassandra for database operations.
 */
@Repository
public interface OrderRepository extends CassandraRepository<HotelBookingOrder, UUID> {
    
    /**
     * Find all orders by exact customer email match.
     * Uses SASI index for efficient lookup.
     * 
     * @param email Customer email address
     * @return List of matching orders
     */
    @Query("SELECT * FROM booking_orders WHERE customer_email = ?0 ALLOW FILTERING")
    List<HotelBookingOrder> findByCustomerEmail(String email);
    
    /**
     * Find orders by customer email with LIKE/CONTAINS support (for fuzzy matching pre-filter).
     * Uses SASI index CONTAINS mode.
     * 
     * @param emailPattern Email pattern to match
     * @return List of candidate orders
     */
    @Query("SELECT * FROM booking_orders WHERE customer_email LIKE ?0 ALLOW FILTERING")
    List<HotelBookingOrder> findByCustomerEmailContaining(String emailPattern);
}
