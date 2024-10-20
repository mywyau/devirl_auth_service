CREATE TABLE business (
    id BIGSERIAL PRIMARY KEY,                             -- Primary key with auto-increment, better scalability with BIGSERIAL
    business_id VARCHAR(255) NOT NULL UNIQUE,
    business_name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE facilities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- Example: "Wi-Fi", "Parking", "Projector"
);

CREATE TABLE amenities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- Example: "Coffee machine", "Printing", "Kitchenette", "Lounge Area" etc.
);


DROP TABLE IF EXISTS workspaces;

-- Create the workspaces table
CREATE TABLE workspaces (
    id SERIAL PRIMARY KEY,                         -- Unique ID for each workspace
    business_id VARCHAR(255) NOT NULL REFERENCES business(business_id) ON DELETE CASCADE,  -- Foreign key to users table (business owners)
    workspace_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,                    -- Name of the workspace
    description TEXT,                              -- Description of the workspace
    address VARCHAR(255) NOT NULL,                 -- Address of the workspace
    city VARCHAR(255) NOT NULL,                    -- City where the workspace is located
    country VARCHAR(100) NOT NULL,                 -- Country where the workspace is located
    postcode VARCHAR(20),                          -- Postcode (optional)
    price_per_day DECIMAL(10, 2) NOT NULL CHECK (price_per_day >= 0),  -- Price per day (non-negative)
    latitude DECIMAL(9, 6) NOT NULL,               -- Latitude for the workspace location
    longitude DECIMAL(9, 6) NOT NULL,              -- Longitude for the workspace location
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Timestamp of workspace creation
);

CREATE TABLE workspace_images (
    id SERIAL PRIMARY KEY,
    workspace_id INT REFERENCES workspaces(id) ON DELETE CASCADE,
    image_url VARCHAR(255) NOT NULL
);

CREATE TABLE workspace_facilities (
    workspace_id INT REFERENCES workspaces(id) ON DELETE CASCADE,
    facility_id INT REFERENCES facilities(id) ON DELETE CASCADE,
    PRIMARY KEY (workspace_id, facility_id) -- Composite primary key
);


CREATE TABLE business_hours (
    id SERIAL PRIMARY KEY,
    workspace_id INT REFERENCES workspaces(id) ON DELETE CASCADE,
    day_of_week VARCHAR(10) NOT NULL,  -- Example values: "Monday", "Tuesday"
    opening_time TIME NOT NULL,
    closing_time TIME NOT NULL
);

CREATE INDEX idx_city ON workspaces (city);
CREATE INDEX idx_price_per_day ON workspaces (price_per_day);
CREATE INDEX idx_lat_lng ON workspaces (latitude, longitude);
