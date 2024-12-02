INSERT INTO business_address (user_id, business_id, business_name, building_name, floor_number, street, city, country, county, postcode)
VALUES
('user_001', 'biz_001', 'Tech Innovators', 'Tech Tower', '5th Floor', '123 Tech Drive', 'San Francisco', 'USA', 'California', '94107'),
('user_002', 'biz_002', 'Green Startups Hub', 'Green Building', '3rd Floor', '456 Greenway Blvd', 'Austin', 'USA', 'Texas', '78701'),
('user_003', 'biz_003', 'Corporate Solutions Inc.', 'Business Plaza', '12th Floor', '789 Business Ave', 'New York', 'USA', 'New York', '10001'),
('user_004', 'biz_004', 'Freelancers Unite', 'Creative Hub', '2nd Floor', '101 Creative Circle', 'Portland', 'USA', 'Oregon', '97209'),
('user_005', 'biz_005', 'Design Studio Hub', 'Artistry Center', '4th Floor', '202 Artistry Lane', 'Los Angeles', 'USA', 'California', '90001');


INSERT INTO business_contact_details (user_id, business_id, business_name, primary_contact, contact_email, contact_phone, website_url)
VALUES
('user_001', 'biz_001', 'Tech Innovators', 'John Doe', 'contact@techinnovators.com', '+1-800-555-001', 'https://techinnovators.com'),
('user_002', 'biz_002', 'Green Startups Hub', 'Jane Smith', 'info@greenstartups.com', '+1-800-555-002', 'https://greenstartups.com'),
('user_003', 'biz_003', 'Corporate Solutions Inc.', 'Michael Brown', 'support@corporatesolutions.com', '+1-800-555-003', 'https://corporatesolutions.com'),
('user_004', 'biz_004', 'Freelancers Unite', 'Sarah Connor', 'hello@freelancersunite.com', '+1-800-555-004', 'https://freelancersunite.com'),
('user_005', 'biz_005', 'Design Studio Hub', 'Emily Davis', 'contact@designstudiohub.com', '+1-800-555-005', 'https://designstudiohub.com');

INSERT INTO business_specs (user_id, business_id, business_name, description, business_type)
VALUES
('user_001', 'biz_001', 'Tech Innovators', 'A tech-focused co-working space.', 'Co-working'),
('user_002', 'biz_002', 'Green Startups Hub', 'A hub for eco-friendly startups.', 'Private Office'),
('user_003', 'biz_003', 'Corporate Solutions Inc.', 'Office solutions for corporate businesses.', 'Corporate'),
('user_004', 'biz_004', 'Freelancers Unite', 'A community-driven co-working space for freelancers.', 'Co-working'),
('user_005', 'biz_005', 'Design Studio Hub', 'A creative space for designers and artists.', 'Creative Studio');
