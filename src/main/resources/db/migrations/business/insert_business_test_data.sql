-- Insert test data into desks
INSERT INTO desks (id, location, status) VALUES ('desk1', 'Zone A', 'available');
INSERT INTO desks (id, location, status) VALUES ('desk2', 'Zone B', 'unavailable');
INSERT INTO desks (id, location, status) VALUES ('desk3', 'Zone A', 'available');

-- Insert test data into bookings
INSERT INTO bookings (id, user_id, desk_id, start_time, end_time)
VALUES ('booking1', 'user1', 'desk1', '2024-10-06 09:00:00', '2024-10-06 12:00:00');
