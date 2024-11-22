DROP TABLE IF EXISTS business_details;

CREATE TABLE business_details (
    id BIGSERIAL PRIMARY KEY,                             -- Primary key with auto-increment, better scalability with BIGSERIAL
    business_id VARCHAR(255) NOT NULL UNIQUE,
    business_name VARCHAR(255) NOT NULL,
    business_type VARCHAR(255) NOT NULL,
    business_industry VARCHAR(255) NOT NULL,
    primary_contact VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS business_address;

CREATE TABLE business_address (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255),
    street VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    county VARCHAR(255),
    postcode VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS desk_listings;

CREATE TABLE desk_listings (
    id SERIAL PRIMARY KEY,                         -- Unique ID for each workspace
    business_id VARCHAR(255),
    workspace_id VARCHAR(255),
    title VARCHAR(50),
    description TEXT,
    desk_type VARCHAR(100),
    quantity INT NOT NULL CHECK (quantity >= 0),   -- Number of desks available, must be non-negative
    price_per_hour DECIMAL(10, 2) CHECK (price_per_day >= 0),  -- Price per day (non-negative)
    price_per_day DECIMAL(10, 2) CHECK (price_per_day >= 0),  -- Price per day (non-negative)
    features TEXT[],                               -- Array of features (e.g., Wi-Fi, Coffee)
    availability JSONB,                            -- Availability info (days, start time, end time)
    rules TEXT,                                    -- Usage rules
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


