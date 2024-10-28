-- Insert regular users
INSERT INTO users (username, password_hash, first_name, last_name, contact_number, email, role, created_at) VALUES
('john_doe', 'hashed_password_1', 'John', 'Doe', '+1234567890', 'john.doe@example.com', 'user', '2024-10-01 10:00:00'),
('jane_smith', 'hashed_password_2', 'Jane', 'Smith', '+1234567891', 'jane.smith@example.com', 'user', '2024-10-02 11:15:00'),
('michael_young', 'hashed_password_3', 'Michael', 'Young', '+1234567892', 'michael.young@business.com', 'business', '2024-10-03 12:30:00'),
('susan_lee', 'hashed_password_4', 'Susan', 'Lee', '+1234567893', 'susan.lee@business.com', 'business', '2024-10-04 14:45:00'),
('admin_tom', 'hashed_password_5', 'Tom', 'Administrator', '+1234567894', 'admin.tom@example.com', 'admin', '2024-10-05 09:00:00'),
('alice_wong', 'hashed_password_6', 'Alice', 'Wong', '+1234567895', 'alice.wong@example.com', 'user', '2024-10-06 08:30:00'),
('charles_jones', 'hashed_password_7', 'Charles', 'Jones', '+1234567896', 'charles.jones@enterprise.com', 'business', '2024-10-07 15:15:00'),
('eva_black', 'hashed_password_8', 'Eva', 'Black', '+1234567897', 'eva.black@example.com', 'user', '2024-10-08 16:20:00'),
('admin_sarah', 'hashed_password_9', 'Sarah', 'Admin', '+1234567898', 'sarah.admin@example.com', 'admin', '2024-10-09 17:45:00'),
('business_jim', 'hashed_password_10', 'Jim', 'Business', '+1234567899', 'jim.business@workplace.com', 'business', '2024-10-10 13:10:00');
