-- Insert sample payments into the payments table
-- Ensure the `booking_id` corresponds to valid records in the `bookings` table

-- Payment for booking 1 (completed)
INSERT INTO payments (booking_id, amount, payment_status, payment_method, transaction_id, created_at)
VALUES
(1, 150.00, 'completed', 'credit card', 'txn_123456', '2024-10-06 10:00:00');

-- Payment for booking 2 (pending)
INSERT INTO payments (booking_id, amount, payment_status, payment_method, transaction_id, created_at)
VALUES
(2, 200.00, 'pending', 'paypal', 'txn_7891011', '2024-10-07 12:00:00');

-- Payment for booking 3 (failed)
INSERT INTO payments (booking_id, amount, payment_status, payment_method, transaction_id, created_at)
VALUES
(3, 75.00, 'failed', 'bank transfer', 'txn_121314', '2024-10-08 14:30:00');

-- Payment for booking 4 (completed)
INSERT INTO payments (booking_id, amount, payment_status, payment_method, transaction_id, created_at)
VALUES
(4, 180.00, 'completed', 'credit card', 'txn_151617', '2024-10-09 11:00:00');
