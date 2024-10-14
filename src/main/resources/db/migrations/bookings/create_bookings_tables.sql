---- Create desks and bookings tables
--CREATE TABLE desks (
--    id VARCHAR(255) PRIMARY KEY,
--    location VARCHAR(255),
--    status VARCHAR(50) NOT NULL
--);
--
--CREATE TABLE desk_address (
--    id VARCHAR(255) PRIMARY KEY,
--    street VARCHAR(255) NOT NULL,
--    city VARCHAR(255) NOT NULL,
--    country VARCHAR(255) NOT NULL,
--    postcode VARCHAR(255) NOT NULL,
--    status VARCHAR(50) NOT NULL,
--    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
--);
--
--CREATE TABLE bookings (
--    id VARCHAR(255) PRIMARY KEY,
--    user_id VARCHAR(255) NOT NULL,
--    desk_id VARCHAR(255),
--    room_id VARCHAR(255),
--    start_time TIMESTAMP NOT NULL,
--    end_time TIMESTAMP NOT NULL,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
--);

CREATE TABLE bookings (
    id SERIAL PRIMARY KEY,           -- Unique ID for each booking
    user_id INT NOT NULL REFERENCES users(id),  -- Foreign key referencing the user who made the booking
    workspace_id INT NOT NULL REFERENCES workspaces(id),  -- Foreign key referencing the workspace being booked
    booking_date DATE NOT NULL,      -- Date of the booking
    start_time TIME NOT NULL,        -- Start time of the booking
    end_time TIME NOT NULL,          -- End time of the booking
    status VARCHAR(50) DEFAULT 'pending',  -- Status of the booking ('pending', 'confirmed', 'cancelled')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of booking creation
);

