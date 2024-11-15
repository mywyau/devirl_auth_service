-- Insert test data into user_login_details
INSERT INTO user_login_details (user_id, username, password_hash, email, role, created_at, updated_at)
VALUES
('user-001', 'jdoe', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'jdoe@example.com', 'Wanderer',  '2023-01-01 12:00:00', '2023-01-01 12:00:00'),
('user-002', 'asmith', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'asmith@example.com', 'Wanderer', '2023-01-02 13:00:00', '2023-01-01 12:00:00'),
('user-003', 'mjones', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'mjones@example.com', 'Wanderer', '2023-01-03 14:00:00', '2023-01-01 12:00:00');

-- Insert test data into wanderer_address
INSERT INTO wanderer_address (user_id, street, city, country, county, postcode, created_at, updated_at)
VALUES
('user-001', '123 Main St', 'Springfield', 'USA', 'Clark', '12345', '2023-01-01 12:05:00', '2023-01-01 12:05:00'),
('user-002', '456 Maple Ave', 'Riverdale', 'USA', 'Cuyahoga', '54321', '2023-01-02 13:05:00', '2023-01-01 12:05:00'),
('user-003', '789 Elm St', 'Metropolis', 'USA', 'Wayne', '67890', '2023-01-03 14:05:00', '2023-01-01 12:05:00');

-- Insert test data into user_contact_details
INSERT INTO wanderer_contact_details (user_id, contact_number, email, created_at, updated_at)
VALUES
('user-001', '555-1234', 'jdoe@example.com', '2023-01-01 12:10:00', '2023-01-01 12:10:00'),
('user-002', '555-5678', 'asmith@example.com', '2023-01-02 13:10:00', '2023-01-01 12:10:00'),
('user-003', '555-9012', 'mjones@example.com', '2023-01-03 14:10:00', '2023-01-01 12:10:00');

-- Insert test data into user_profile
INSERT INTO user_profile (userId, username, password_hash, first_name, last_name, street, city, country, county, postcode, contact_number, email, role, created_at, updated_at)
VALUES
('user-001', 'jdoe', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'John', 'Doe', '123 Main St', 'Springfield', 'USA', 'Clark', '12345', '555-1234', 'jdoe@example.com', 'Wanderer', '2023-01-01 12:15:00', '2023-01-01 12:15:00'),
('user-002', 'asmith', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'Alice', 'Smith', '456 Maple Ave', 'Riverdale', 'USA', 'Cuyahoga', '54321', '555-5678', 'asmith@example.com', 'Wanderer', '2023-01-02 13:15:00', '2023-01-02 13:15:00'),
('user-003', 'mjones', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'Mary', 'Jones', '789 Elm St', 'Metropolis', 'USA', 'Wayne', '67890', '555-9012', 'mjones@example.com', 'Business', '2023-01-03 14:15:00', '2023-01-03 14:15:00'),
('user-004', 'mjones', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'Mary', 'Jones', '789 Elm St', 'Metropolis', 'USA', 'Wayne', '67890', '555-9012', 'mjones@example.com', 'Business', '2023-01-03 15:15:00', '2023-01-03 15:15:00'),
('user-005', 'mjones', '$2b$10$ABCDEFGHIJKLMNOPQRSTUVWX/abcdefghijklmnopqrstuvwx', 'Mary', 'Jones', '789 Elm St', 'Metropolis', 'USA', 'Wayne', '67890', '555-9012', 'mjones@example.com', 'Business', '2023-01-03 16:15:00', '2023-01-03 16:15:00');
