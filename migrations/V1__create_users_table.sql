-- Drop the reviews table if it exists (for reinitialization)
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,         -- Unique ID for each user
    username VARCHAR(255) NOT NULL,    -- Name of the user
    password_hash TEXT NOT NULL,   -- Hashed password for security
    first_name VARCHAR(255) NOT NULL,    -- Name of the user
    last_name VARCHAR(255) NOT NULL,    -- Name of the user
    contact_number VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,  -- Email for login
    role VARCHAR(50) DEFAULT 'user',  -- User role ('user', 'business', 'admin')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of account creation
);

-- Create indexes to optimize queries on users table
CREATE INDEX idx_email ON users (email);
CREATE INDEX idx_role ON users (role);