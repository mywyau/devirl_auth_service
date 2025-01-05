INSERT INTO business_address (user_id, business_id, building_name, floor_number, street, city, country, county, postcode)
VALUES
('user_001', 'biz_001', 'Tech Tower', '5th Floor', '123 Tech Drive', 'San Francisco', 'USA', 'California', '94107'),
('user_002', 'biz_002', 'Green Building', '3rd Floor', '456 Greenway Blvd', 'Austin', 'USA', 'Texas', '78701'),
('user_003', 'biz_003', 'Business Plaza', '12th Floor', '789 Business Ave', 'New York', 'USA', 'New York', '10001'),
('user_004', 'biz_004', 'Creative Hub', '2nd Floor', '101 Creative Circle', 'Portland', 'USA', 'Oregon', '97209'),
('user_005', 'biz_005', 'Artistry Center', '4th Floor', '202 Artistry Lane', 'Los Angeles', 'USA', 'California', '90001');


INSERT INTO business_contact_details (user_id, business_id, primary_contact_first_name, primary_contact_last_name, contact_email, contact_number, website_url)
VALUES
('user_001', 'biz_001', 'John', 'Doe', 'contact@techinnovators.com', '+1-800-555-001', 'https://techinnovators.com'),
('user_002', 'biz_002', 'Jane', 'Smith', 'info@greenstartups.com', '+1-800-555-002', 'https://greenstartups.com'),
('user_003', 'biz_003', 'Michael', 'Brown', 'support@corporatesolutions.com', '+1-800-555-003', 'https://corporatesolutions.com'),
('user_004', 'biz_004', 'Sarah',  'Connor', 'hello@freelancersunite.com', '+1-800-555-004', 'https://freelancersunite.com'),
('user_005', 'biz_005', 'Emily', 'Davis', 'contact@designstudiohub.com', '+1-800-555-005', 'https://designstudiohub.com');

INSERT INTO business_specifications (user_id, business_id, business_name, description, availability)
VALUES
('user_001', 'biz_001', 'Tech Innovators', 'A tech-focused co-working space.', '{"days": ["Monday", "Tuesday"], "startTime": "08:00:00", "endTime": "18:00:00"}'::JSONB),
('user_002', 'biz_002', 'Green Startups Hub', 'A hub for eco-friendly startups.', '{"days": ["Monday", "Tuesday"], "startTime": "08:00:00", "endTime": "18:00:00"}'::JSONB),
('user_003', 'biz_003', 'Corporate Solutions Inc.', 'Office solutions for corporate businesses.', '{"days": ["Monday", "Tuesday"], "startTime": "08:00:00", "endTime": "18:00:00"}'::JSONB),
('user_004', 'biz_004', 'Freelancers Unite', 'A community-driven co-working space for freelancers.', '{"days": ["Monday", "Tuesday"], "startTime": "08:00:00", "endTime": "18:00:00"}'::JSONB),
('user_005', 'biz_005', 'Design Studio Hub', 'A creative space for designers and artists.', '{"days": ["Monday", "Tuesday"], "startTime": "08:00:00", "endTime": "18:00:00"}'::JSONB);
