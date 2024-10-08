-- Drop tables if they exist (optional, for reinitialization)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS desks;

-- Create the desks table
CREATE TABLE desks (
    id VARCHAR(255) PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

-- Create the bookings table
CREATE TABLE bookings (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    desk_id VARCHAR(255),
    room_id VARCHAR(255),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
);

-- Create indexes for faster lookups (must be separate from the CREATE TABLE command)
CREATE INDEX idx_user_id ON bookings (user_id);
CREATE INDEX idx_desk_id ON bookings (desk_id);

-- Insert test data into the desks table
INSERT INTO desks (id, location, status) VALUES ('desk1', 'Zone A', 'available');
INSERT INTO desks (id, location, status) VALUES ('desk2', 'Zone B', 'unavailable');
INSERT INTO desks (id, location, status) VALUES ('desk3', 'Zone A', 'available');

---- Insert test data into the bookings table
--INSERT INTO bookings (id, user_id, desk_id, room_id, start_time, end_time)
--VALUES ('booking1', 'user1', 'desk1', 'room1', '2024-10-06 09:00:00', '2024-10-06 12:00:00');
