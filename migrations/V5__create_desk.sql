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

