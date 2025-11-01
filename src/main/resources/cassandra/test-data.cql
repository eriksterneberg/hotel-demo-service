-- Test data for hotel booking orders
USE hotel_demo;

-- Insert test orders for exact match testing
INSERT INTO booking_orders (
    order_id, customer_email, customer_email_encrypted,
    booking_date_start, booking_date_end,
    hotel_name, hotel_address, room_type,
    guest_names, payment_method, total_amount,
    order_status, created_at
) VALUES (
    550e8400-e29b-41d4-a716-446655440000,
    'john.doe@example.com',
    'ENC(encrypted_john.doe@example.com)',
    '2025-12-24 15:00:00',
    '2025-12-27 11:00:00',
    'Grand Plaza Hotel',
    '123 Main Street, New York, NY 10001, USA',
    'Deluxe Suite',
    ['John Doe', 'Jane Doe'],
    'Credit Card - Visa',
    899.99,
    'CONFIRMED',
    '2025-11-01 10:00:00'
);

-- Insert order for fuzzy match testing
INSERT INTO booking_orders (
    order_id, customer_email, customer_email_encrypted,
    booking_date_start, booking_date_end,
    hotel_name, hotel_address, room_type,
    guest_names, payment_method, total_amount,
    order_status, created_at
) VALUES (
    650e8400-e29b-41d4-a716-446655440001,
    'frequent.traveler@example.com',
    'ENC(encrypted_frequent.traveler@example.com)',
    '2025-11-15 14:00:00',
    '2025-11-18 10:00:00',
    'Seaside Resort',
    '456 Beach Road, Miami, FL 33139, USA',
    'Ocean View King',
    ['Alex Traveler'],
    'Credit Card - Mastercard',
    650.00,
    'CONFIRMED',
    '2025-10-15 09:30:00'
);

-- Insert another order for same frequent traveler
INSERT INTO booking_orders (
    order_id, customer_email, customer_email_encrypted,
    booking_date_start, booking_date_end,
    hotel_name, hotel_address, room_type,
    guest_names, payment_method, total_amount,
    order_status, created_at
) VALUES (
    750e8400-e29b-41d4-a716-446655440002,
    'frequent.traveler@example.com',
    'ENC(encrypted_frequent.traveler@example.com)',
    '2025-10-20 16:00:00',
    '2025-10-22 12:00:00',
    'Mountain Lodge',
    '789 Alpine Way, Aspen, CO 81611, USA',
    'Standard Double',
    ['Alex Traveler', 'Sam Traveler'],
    'PayPal',
    320.00,
    'CHECKED_OUT',
    '2025-09-20 14:45:00'
);

-- Insert more test data
INSERT INTO booking_orders (
    order_id, customer_email, customer_email_encrypted,
    booking_date_start, booking_date_end,
    hotel_name, hotel_address, room_type,
    guest_names, payment_method, total_amount,
    order_status, created_at
) VALUES (
    850e8400-e29b-41d4-a716-446655440003,
    'jane.smith@example.com',
    'ENC(encrypted_jane.smith@example.com)',
    '2025-12-15 16:00:00',
    '2025-12-20 10:00:00',
    'City Center Hotel',
    '100 Downtown Ave, San Francisco, CA 94102, USA',
    'Executive Suite',
    ['Jane Smith'],
    'Credit Card - Amex',
    1250.00,
    'PENDING',
    '2025-11-01 11:15:00'
);

INSERT INTO booking_orders (
    order_id, customer_email, customer_email_encrypted,
    booking_date_start, booking_date_end,
    hotel_name, hotel_address, room_type,
    guest_names, payment_method, total_amount,
    order_status, created_at
) VALUES (
    950e8400-e29b-41d4-a716-446655440004,
    'test.user@hotel.com',
    'ENC(encrypted_test.user@hotel.com)',
    '2025-11-30 15:00:00',
    '2025-12-02 11:00:00',
    'Airport Inn',
    '500 Airport Blvd, Los Angeles, CA 90045, USA',
    'Standard Queen',
    ['Test User'],
    'Debit Card',
    199.99,
    'CONFIRMED',
    '2025-10-28 16:20:00'
);
