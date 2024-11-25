DROP TABLE IF EXISTS office_details;

CREATE TABLE office_details (
    id BIGSERIAL PRIMARY KEY,                             -- Primary key with auto-increment, better scalability with BIGSERIAL
    office_id VARCHAR(255) NOT NULL UNIQUE,
    office_name VARCHAR(255) NOT NULL,
    office_type VARCHAR(255) NOT NULL,
    primary_contact VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS office_address;

CREATE TABLE office_address (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    office_id VARCHAR(255),
    street VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    county VARCHAR(255),
    postcode VARCHAR(255),
    building_name VARCHAR(255),
    floor_number VARCHAR(50),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS office_listing;

CREATE TABLE office_listing (
    id SERIAL PRIMARY KEY,                              -- Unique ID for each workspace
    office_id VARCHAR(255),
    office_name VARCHAR(255),
    description TEXT,
    office_type VARCHAR(100),
    capacity INT,
    amenities TEXT[],                                   -- Array of amenities (e.g., Wi-Fi, Coffee)
    availability JSONB,                                 -- Availability info (days, opening time, closing time)
    rules TEXT,                                         -- Usage rules
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


