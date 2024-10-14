CREATE TABLE availability (
    id SERIAL PRIMARY KEY,           -- Unique ID for each availability record
    workspace_id INT NOT NULL REFERENCES workspaces(id),  -- Foreign key referencing the workspace
    available_from TIMESTAMP NOT NULL,  -- Start of the availability window
    available_until TIMESTAMP NOT NULL  -- End of the availability window
);
