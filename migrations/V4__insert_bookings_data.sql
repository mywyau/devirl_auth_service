-- Insert initial workspaces
INSERT INTO workspaces (business_id, name, description, address, city, country, postcode, price_per_day, latitude, longitude, image_url)
VALUES
(1, 'Downtown Office', 'A modern office in the city center', '123 Main St', 'New York', 'USA', '10001', 50.00, 40.7128, -74.0060, 'https://example.com/downtown-office.jpg'),
(2, 'Uptown Workspace', 'Spacious shared workspace with amenities', '456 Elm St', 'New York', 'USA', '10002', 30.00, 40.7306, -73.9352, 'https://example.com/uptown-workspace.jpg');

-- Insert initial bookings (ensure these correspond to valid user_id and workspace_id)
INSERT INTO bookings (user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
VALUES
(1, 1, '2024-10-10', '09:00:00', '12:00:00', 'Confirmed', NOW()),
(2, 2, '2024-10-11', '13:00:00', '16:00:00', 'Pending', NOW());
(3, 3, '2024-10-12', '12:00:00', '17:00:00', 'Pending', NOW());
