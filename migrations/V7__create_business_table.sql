CREATE TABLE businesses (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Foreign key to users table
    name VARCHAR(255) NOT NULL,                                    -- Business name
    contact_email VARCHAR(255),
    contact_phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE facilities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- Example: "Wi-Fi", "Parking", "Projector"
);

CREATE TABLE workspace_facilities (
    workspace_id INT REFERENCES workspaces(id) ON DELETE CASCADE,
    facility_id INT REFERENCES facilities(id) ON DELETE CASCADE,
    PRIMARY KEY (workspace_id, facility_id) -- Composite primary key
);


CREATE TABLE amenities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- Example: "Coffee machine", "Printing", "Kitchenette", "Lounge Area" etc.
);