-- Drop the reviews table if it exists (for reinitialization)

DROP TABLE IF EXISTS user_login_details;

CREATE TABLE user_login_details (
    id BIGSERIAL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


DROP TABLE IF EXISTS user_address;

CREATE TABLE user_address (
    id BIGSERIAL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    county VARCHAR(255) NOT NULL,
    postcode VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DROP TABLE IF EXISTS user_contact_details;

CREATE TABLE user_contact_details (
    id BIGSERIAL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    contact_number VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


DROP TABLE IF EXISTS user_profile;

CREATE TABLE user_profile (
    id BIGSERIAL PRIMARY KEY,
    userId VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    password_hash TEXT NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    country VARCHAR(255) NOT NULL,
    county VARCHAR(255) NOT NULL,
    postcode VARCHAR(255) NOT NULL,
    contact_number VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'wanderer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes to optimize queries on users table
--CREATE INDEX idx_email ON users (email);
--CREATE INDEX idx_role ON users (role);