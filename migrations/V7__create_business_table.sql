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

CREATE TABLE workspace_facilities (
    workspace_id INT REFERENCES workspaces(id) ON DELETE CASCADE,
    facility_id INT REFERENCES facilities(id) ON DELETE CASCADE,
    PRIMARY KEY (workspace_id, facility_id) -- Composite primary key
);


CREATE TABLE amenities (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE -- Example: "Coffee machine", "Printing", "Kitchenette", "Lounge Area" etc.
);