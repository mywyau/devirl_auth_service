-- Drop the payments table if it exists (for reinitialization)
DROP TABLE IF EXISTS payments;

-- Create the payments table
CREATE TABLE payments (
    id SERIAL PRIMARY KEY,                               -- Unique ID for each payment
    booking_id INT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,  -- Foreign key referencing the booking, cascade delete if booking is deleted
    amount DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),  -- Amount paid (non-negative)
    payment_status VARCHAR(50) DEFAULT 'pending' CHECK (payment_status IN ('pending', 'completed', 'failed')),  -- Status of the payment
    payment_method VARCHAR(50),                          -- Payment method used (e.g., 'credit card', 'paypal')
    transaction_id VARCHAR(255),                         -- External transaction ID (optional)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP       -- Timestamp of payment creation
);

-- Indexes for faster queries
CREATE INDEX idx_booking_id ON payments (booking_id);
CREATE INDEX idx_payment_status ON payments (payment_status);
