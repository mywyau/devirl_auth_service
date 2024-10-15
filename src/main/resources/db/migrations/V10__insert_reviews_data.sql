-- Insert sample reviews into the reviews table
-- Ensure the `user_id` and `workspace_id` values correspond to valid entries in the `users` and `workspaces` tables

-- Reviews for workspace 1
INSERT INTO reviews (user_id, workspace_id, rating, comment, created_at)
VALUES
(1, 1, 5, 'Amazing workspace, very productive environment!', '2024-10-06 10:00:00'),
(2, 1, 4, 'Great location, but could use faster Wi-Fi.', '2024-10-07 11:30:00');

-- Reviews for workspace 2
INSERT INTO reviews (user_id, workspace_id, rating, comment, created_at)
VALUES
(1, 2, 3, 'Decent workspace, but a bit noisy during the afternoon.', '2024-10-08 14:45:00'),
(3, 2, 4, 'Comfortable seating and good amenities.', '2024-10-09 09:15:00');

-- Reviews for workspace 3
INSERT INTO reviews (user_id, workspace_id, rating, comment, created_at)
VALUES
(2, 3, 2, 'Needs better air conditioning and maintenance.', '2024-10-10 16:00:00'),
(3, 3, 5, 'Fantastic experience! Will book again!', '2024-10-11 12:00:00');
