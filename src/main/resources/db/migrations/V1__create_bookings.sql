-- Drop the bookings and workspaces tables if they exist
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS workspaces;

-- Create the workspaces table
CREATE TABLE workspaces (
    id SERIAL PRIMARY KEY,                         -- Unique ID for each workspace
    business_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Foreign key to users table (business owners)
    name VARCHAR(255) NOT NULL,                    -- Name of the workspace
    description TEXT,                              -- Description of the workspace
    address VARCHAR(255) NOT NULL,                 -- Address of the workspace
    city VARCHAR(255) NOT NULL,                    -- City where the workspace is located
    country VARCHAR(100) NOT NULL,                 -- Country where the workspace is located
    postcode VARCHAR(20),                          -- Postcode (optional)
    price_per_day DECIMAL(10, 2) NOT NULL CHECK (price_per_day >= 0),  -- Price per day (non-negative)
    latitude DECIMAL(9, 6) NOT NULL,               -- Latitude for the workspace location
    longitude DECIMAL(9, 6) NOT NULL,              -- Longitude for the workspace location
    image_url VARCHAR(255),                        -- URL for the workspace image
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp of workspace creation
);

-- Create the bookings table
CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,                         -- Unique ID for each booking
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Foreign key to users table (user who made the booking)
    workspace_id INT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,  -- Foreign key to workspaces table (workspace being booked)
    booking_date DATE NOT NULL,                    -- Date of the booking
    start_time TIME NOT NULL,                      -- Start time of the booking
    end_time TIME NOT NULL CHECK (end_time > start_time),  -- End time of the booking (must be after start time)
    status VARCHAR(50) DEFAULT 'pending' CHECK (status IN ('pending', 'confirmed', 'cancelled')),  -- Status of the booking
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp of booking creation
);

-- Indexes for faster querying
CREATE INDEX idx_user_id ON bookings (user_id);
CREATE INDEX idx_workspace_id ON bookings (workspace_id);
CREATE INDEX idx_booking_date ON bookings (booking_date);
CREATE INDEX idx_city ON workspaces (city);
CREATE INDEX idx_price_per_day ON workspaces (price_per_day);
CREATE INDEX idx_lat_lng ON workspaces (latitude, longitude);
