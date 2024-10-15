-- Create the availability table
CREATE TABLE availability (
    id SERIAL PRIMARY KEY,                           -- Unique ID for each availability record
    workspace_id INT NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,  -- Foreign key referencing the workspace
    available_from TIMESTAMP NOT NULL,               -- Start of the availability window
    available_until TIMESTAMP NOT NULL CHECK (available_until > available_from) -- End of the availability window (must be after the start)
);

-- Optional: Create an index to optimize searches for availability
CREATE INDEX idx_workspace_availability ON availability (workspace_id, available_from, available_until);
