-- Create desks and bookings tables
CREATE TABLE desks (
    id VARCHAR(255) PRIMARY KEY,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
);

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
