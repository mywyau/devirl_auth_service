-- Insert initial workspaces
INSERT INTO workspaces (business_id, name, description, address, city, country, postcode, price_per_day, latitude, longitude, image_url)
VALUES
(1, 'Downtown Office', 'A modern office in the city center', '123 Main St', 'New York', 'USA', '10001', 50.00, 40.7128, -74.0060, 'https://example.com/downtown-office.jpg'),
(2, 'Uptown Workspace', 'Spacious shared workspace with amenities', '456 Elm St', 'New York', 'USA', '10002', 30.00, 40.7306, -73.9352, 'https://example.com/uptown-workspace.jpg'),
(3, 'London Workspace', 'Spacious shared workspace with amenities', 'Canary Wharf', 'London', 'United Kingdom', 'NW1 4NP', 30.00, 40.7306, -73.9352, 'https://example.com/london-workspace.jpg'),
(4, 'New York Workspace', 'Spacious shared workspace with amenities', '456 Elm St', 'New York', 'USA', '10002', 30.00, 40.7306, -73.9352, 'https://example.com/new-york-workspace.jpg'),
(5, 'Cardiff Workspace', 'Spacious shared workspace with amenities', '456 Cardiff Bay', 'Cardiff', 'United Kingdom', 'CF3 3NJ', 30.00, 40.7306, -73.9352, 'https://example.com/cardiff-workspace.jpg');

-- Insert initial bookings (ensure these correspond to valid user_id and workspace_id)
INSERT INTO bookings (booking_id, booking_name, user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
VALUES
('booking_1', 'mikey party booking',  1, 1, '2024-10-10', '09:00:00', '12:00:00', 'Confirmed', NOW()),
('booking_2', 'mikey work booking',   2, 2, '2024-10-11', '13:00:00', '16:00:00', 'Pending', NOW()),
('booking_3', 'mikey coffee booking', 3, 3, '2024-10-12', '12:00:00', '17:00:00', 'Pending', NOW()),
('booking_4', 'mikey coffee booking', 4, 4, '2024-10-12', '12:00:00', '17:00:00', 'Pending', NOW()),
('booking_5', 'mikey coffee booking', 5, 5, '2024-10-12', '12:00:00', '17:00:00', 'Pending', NOW());
