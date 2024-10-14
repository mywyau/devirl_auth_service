
CREATE TABLE workspaces (
    id SERIAL PRIMARY KEY,           -- Unique ID for each workspace
    business_id INT NOT NULL REFERENCES users(id),  -- Foreign key referencing the business (user) who owns the workspace
    name VARCHAR(255) NOT NULL,      -- Name of the workspace
    description TEXT,                -- Description of the workspace
    address VARCHAR(255) NOT NULL,   -- Address of the workspace
    city VARCHAR(255) NOT NULL,      -- City where the workspace is located
    country VARCHAR(100) NOT NULL,   -- Country where the workspace is located
    postcode VARCHAR(20),            -- Postcode (optional)
    price_per_day DECIMAL(10, 2) NOT NULL,  -- Price per day
    latitude DECIMAL(9, 6) NOT NULL,  -- Latitude for the workspace location
    longitude DECIMAL(9, 6) NOT NULL, -- Longitude for the workspace location
    image_url VARCHAR(255),          -- URL for the workspace image
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of workspace creation
);

