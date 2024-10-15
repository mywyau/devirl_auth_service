-- Drop the reviews table if it exists (for reinitialization)
DROP TABLE IF EXISTS reviews;

-- Create the reviews table
CREATE TABLE reviews (
    id SERIAL PRIMARY KEY,                               -- Unique ID for each review
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,  -- Foreign key referencing the user who wrote the review
    workspace_id INT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,  -- Foreign key referencing the workspace being reviewed
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),  -- Rating (1 to 5 stars)
    comment TEXT,                                          -- Optional review comment
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP         -- Timestamp of review creation
);

-- Indexes for faster queries
CREATE INDEX idx_user_id ON reviews (user_id);
CREATE INDEX idx_workspace_id ON reviews (workspace_id);
CREATE INDEX idx_rating ON reviews (rating);
