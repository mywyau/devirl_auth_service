DROP TABLE IF EXISTS office_details;

CREATE TABLE office_contact_details (
    id BIGSERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    office_id VARCHAR(255) NOT NULL UNIQUE,
    primary_contact_first_name VARCHAR(255),
    primary_contact_last_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_number VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS office_address;

CREATE TABLE office_address (
    id BIGSERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    office_id VARCHAR(255) NOT NULL UNIQUE,
    building_name VARCHAR(255),
    floor_number VARCHAR(50),
    street VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    county VARCHAR(255),
    postcode VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS office_specs;

CREATE TABLE office_specifications (
    id SERIAL PRIMARY KEY,
    business_id VARCHAR(255) NOT NULL,
    office_id VARCHAR(255) NOT NULL UNIQUE,
    office_name VARCHAR(255),
    description TEXT,
    office_type VARCHAR(100),
    number_of_floors INT,
    total_desks INT,
    capacity INT,
    amenities TEXT[],
    availability JSONB,
    rules TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


