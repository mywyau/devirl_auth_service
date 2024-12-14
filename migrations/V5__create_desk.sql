DROP TABLE IF EXISTS desk_listings;

CREATE TABLE desk_listings (
    id SERIAL PRIMARY KEY,
    business_id VARCHAR(255),
    workspace_id VARCHAR(255),
    title VARCHAR(50),
    description TEXT,
    desk_type VARCHAR(100),
    quantity INT NOT NULL CHECK (quantity >= 0),
    price_per_hour DECIMAL(10, 2) CHECK (price_per_day >= 0),
    price_per_day DECIMAL(10, 2) CHECK (price_per_day >= 0),
    features TEXT[],
    availability JSONB,
    rules TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

