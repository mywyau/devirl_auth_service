-- Insert availability data for existing workspaces
-- Assuming workspace IDs 1 and 2 already exist in the workspaces table

-- Workspace 1 Availability
INSERT INTO availability (workspace_id, available_from, available_until) VALUES
(1, '2024-10-10 09:00:00', '2024-10-10 17:00:00'),  -- Full day on Oct 10, 2024
(1, '2024-10-11 09:00:00', '2024-10-11 13:00:00'),  -- Half day on Oct 11, 2024
(1, '2024-10-12 08:00:00', '2024-10-12 12:00:00');  -- Morning slot on Oct 12, 2024


-- Workspace 2 Availability
INSERT INTO availability (workspace_id, available_from, available_until)
VALUES
(2, '2024-10-11 09:00:00', '2024-10-11 17:00:00'),  -- Full day on Oct 11, 2024
(2, '2024-10-12 08:00:00', '2024-10-12 15:00:00'),  -- Daytime slot on Oct 12, 2024
(2, '2024-10-13 10:00:00', '2024-10-13 18:00:00');  -- Full day on Oct 13, 2024