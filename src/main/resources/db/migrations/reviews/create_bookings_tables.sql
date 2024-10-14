CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,           -- Unique ID for each review
    user_id INT NOT NULL REFERENCES users(id),  -- Foreign key referencing the user who wrote the review
    workspace_id INT NOT NULL REFERENCES workspaces(id),  -- Foreign key referencing the workspace being reviewed
    rating INT CHECK (rating >= 1 AND rating <= 5),  -- Rating (1 to 5 stars)
    comment TEXT,                    -- Review comment (optional)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP  -- Timestamp of review creation
);
