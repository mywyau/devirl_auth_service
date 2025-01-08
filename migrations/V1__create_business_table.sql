DROP TABLE IF EXISTS business_address;

CREATE TABLE business_address (
    id BIGSERIAL PRIMARY KEY UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    business_id VARCHAR(255) NOT NULL,
    building_name VARCHAR(255),
    floor_number VARCHAR(255),
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

DROP TABLE IF EXISTS business_contact_details;

CREATE TABLE business_contact_details (
    id BIGSERIAL PRIMARY KEY UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    business_id VARCHAR(255) NOT NULL,
    primary_contact_first_name VARCHAR(255),
    primary_contact_last_name VARCHAR(255),
    contact_email VARCHAR(255),
    contact_number VARCHAR(20),
    website_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS business_specifications;

CREATE TABLE business_specifications (
    id SERIAL PRIMARY KEY UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    business_id VARCHAR(255) NOT NULL,
    business_name VARCHAR(255) NOT NULL,
    description TEXT,
    availability JSONB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

