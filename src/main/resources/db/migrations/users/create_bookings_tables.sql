CREATE TABLE users (
    id SERIAL PRIMARY KEY,         -- Unique ID for each user
    name VARCHAR(255) NOT NULL,    -- Name of the user
    email VARCHAR(255) UNIQUE NOT NULL,  -- Email for login
    password_hash TEXT NOT NULL,   -- Hashed password for security
    role VARCHAR(50) DEFAULT 'user',  -- User role ('user', 'business', 'admin')
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of account creation
);
