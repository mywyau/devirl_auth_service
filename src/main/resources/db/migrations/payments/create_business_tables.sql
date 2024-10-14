CREATE TABLE payments (
    id SERIAL PRIMARY KEY,           -- Unique ID for each payment
    booking_id INT NOT NULL REFERENCES bookings(id),  -- Foreign key referencing the booking being paid for
    amount DECIMAL(10, 2) NOT NULL,  -- Amount paid
    payment_status VARCHAR(50) DEFAULT 'pending',  -- Status of the payment ('pending', 'completed', 'failed')
    payment_method VARCHAR(50),      -- Payment method used (e.g., 'credit card', 'paypal')
    transaction_id VARCHAR(255),     -- External transaction ID (if available)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of payment creation
);
