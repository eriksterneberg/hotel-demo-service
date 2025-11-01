package com.hotel.demo.model.entity;

/**
 * Order status enum representing the lifecycle of a hotel booking order.
 * 
 * <p>Valid state transitions:
 * <ul>
 *   <li>PENDING → CONFIRMED, CANCELLED</li>
 *   <li>CONFIRMED → CHECKED_IN, CANCELLED, NO_SHOW</li>
 *   <li>CHECKED_IN → CHECKED_OUT</li>
 *   <li>CANCELLED, CHECKED_OUT, NO_SHOW are terminal states</li>
 * </ul>
 */
public enum OrderStatus {
    /** Initial state after booking creation */
    PENDING,
    
    /** Payment processed, booking confirmed */
    CONFIRMED,
    
    /** Guest checked in to hotel */
    CHECKED_IN,
    
    /** Guest checked out, booking completed */
    CHECKED_OUT,
    
    /** Booking cancelled by customer or system */
    CANCELLED,
    
    /** Guest did not check in */
    NO_SHOW
}
