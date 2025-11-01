-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS hotel_demo 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

-- Use keyspace
USE hotel_demo;

-- Create booking_orders table
CREATE TABLE IF NOT EXISTS booking_orders (
    order_id UUID PRIMARY KEY,
    customer_email TEXT,
    customer_email_encrypted TEXT,
    booking_date_start TIMESTAMP,
    booking_date_end TIMESTAMP,
    hotel_name TEXT,
    hotel_address TEXT,
    room_type TEXT,
    guest_names LIST<TEXT>,
    payment_method TEXT,
    total_amount DECIMAL,
    order_status TEXT,
    created_at TIMESTAMP
);

-- Create standard index for email search (SASI disabled in Cassandra 4.1 by default)
CREATE INDEX IF NOT EXISTS email_idx ON hotel_demo.booking_orders (customer_email);

-- Create indexes for common queries
CREATE INDEX IF NOT EXISTS status_idx ON hotel_demo.booking_orders (order_status);
CREATE INDEX IF NOT EXISTS created_idx ON hotel_demo.booking_orders (created_at);
