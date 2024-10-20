-- Insert initial bookings (ensure these correspond to valid user_id and workspace_id)
INSERT INTO bookings (booking_id, booking_name, user_id, workspace_id, booking_date, start_datetime, end_datetime, status, created_at)
VALUES
('booking_1', 'mikey party booking',  1, 1, '2024-10-10', '2024-10-10 09:00:00', '2024-10-10 12:00:00', 'Confirmed', NOW()),
('booking_2', 'mikey work booking',   2, 2, '2024-10-11', '2024-10-11 13:00:00', '2024-10-11 16:00:00', 'Pending', NOW()),
('booking_3', 'mikey coffee booking', 3, 3, '2024-10-12', '2024-10-12 12:00:00', '2024-10-12 17:00:00', 'Pending', NOW()),
('booking_4', 'mikey coffee booking', 4, 4, '2024-10-13', '2024-10-13 12:00:00', '2024-10-13 17:00:00', 'Pending', NOW()),
('booking_5', 'mikey coffee booking', 5, 5, '2024-10-14', '2024-10-14 12:00:00', '2024-10-14 17:00:00', 'Pending', NOW());
