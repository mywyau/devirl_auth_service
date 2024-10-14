-- Create desks and bookings tables
CREATE TABLE company (
    id VARCHAR(255) PRIMARY KEY,
    company_name VARCHAR(255) PRIMARY KEY
    FOREIGN KEY (address) REFERENCES company_address(id) ON DELETE SET NULL
);

CREATE TABLE company_address (
    id VARCHAR(255) PRIMARY KEY,
    street VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    postcode VARCHAR(255)

);

CREATE TABLE desk (
    id VARCHAR(255) PRIMARY KEY,
    desk_number VARCHAR(255),
    room_id VARCHAR(255),
    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
);

CREATE TABLE room (
    id VARCHAR(255) PRIMARY KEY,
    room_number VARCHAR(255),
    room_id VARCHAR(255),
    room_type VARCHAR(255),
    floor VARCHAR(255),
    status VARCHAR(50)
    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
);

CREATE TABLE office (
    id VARCHAR(255) PRIMARY KEY,
    office_number VARCHAR(255),
    office_id VARCHAR(255),
    floor VARCHAR(255),
    office_type VARCHAR(255),
    status VARCHAR(50)
    FOREIGN KEY (desk_id) REFERENCES desks(id) ON DELETE SET NULL
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

