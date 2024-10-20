-- Drop the bookings and workspaces tables if they exist
DROP TABLE IF EXISTS bookings;

-- Create the bookings table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,
    booking_id VARCHAR(255) NOT NULL,
    booking_name VARCHAR(255) NOT NULL,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Foreign key to users table (user who made the booking)
    workspace_id INT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,  -- Foreign key to workspaces table (workspace being booked)
    booking_date DATE NOT NULL,                    -- Date of the booking
    start_datetime TIMESTAMP NOT NULL,  -- Start date and time (must be in future)
    end_datetime TIMESTAMP NOT NULL CHECK (end_datetime > start_datetime),           -- End time must be after start
    status VARCHAR(50) DEFAULT 'Pending' CHECK (status IN ('Pending', 'Confirmed', 'Cancelled')),  -- Status of the booking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp of booking creation
);

-- Indexes for faster querying
CREATE INDEX idx_user_id_bookings ON bookings (user_id);
CREATE INDEX idx_workspace_id_bookings ON bookings (workspace_id);
CREATE INDEX idx_booking_date ON bookings (booking_date);

