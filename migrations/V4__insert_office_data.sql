INSERT INTO office_contact_details (
    office_id,
    business_id,
    primary_contact_first_name,
    primary_contact_last_name,
    contact_email,
    contact_number
) VALUES
    ('OFF001', 'BUS001', 'John', 'Doe', 'john.doe@example.com', '+1234567890'),
    ('OFF002', 'BUS002', 'Jane', 'Smith', 'jane.smith@example.com', '+1234567891'),
    ('OFF003', 'BUS003', 'Emily', 'Davis', 'emily.davis@example.com', '+1234567892'),
    ('OFF004', 'BUS004', 'Michael', 'Brown', 'michael.brown@example.com', '+1234567893'),
    ('OFF005', 'BUS005', 'Sarah', 'Johnson', 'sarah.johnson@example.com', '+1234567894'),
    ('OFF006', 'BUS006', 'Chris', 'Wilson', 'chris.wilson@example.com', '+1234567895'),
    ('OFF007', 'BUS007', 'Anna', 'Taylor', 'anna.taylor@example.com', '+1234567896'),
    ('OFF008', 'BUS008', 'David', 'Lee', 'david.lee@example.com', '+1234567897'),
    ('OFF009', 'BUS009', 'Emma', 'Walker', 'emma.walker@example.com', '+1234567898'),
    ('OFF010', 'BUS010', 'Daniel', 'Hall', 'daniel.hall@example.com', '+1234567899');


INSERT INTO office_address (
    business_id,
    office_id,
    building_name,
    floor_number,
    street,
    city,
    country,
    county,
    postcode,
    latitude,
    longitude,
    created_at,
    updated_at
) VALUES (
    'business123',
    'office456',
    'Empire State Building',
    '5th Floor',
    '123 Main Street',
    'New York',
    'USA',
    'Manhattan',
    '10001',
    40.748817,
    -73.985428,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO office_specs (
    business_id,
    office_id,
    office_name,
    description,
    office_type,
    number_of_floors,
    total_desks,
    capacity,
    amenities,
    availability,
    rules
) VALUES
(
    'BUS001', 'OFF001', 'Downtown Workspace', 'A modern co-working space.', 'co-working', 2, 50, 100,
    ARRAY['WiFi', 'Coffee', 'Meeting Rooms'],
    '{"monday": "8:00-18:00", "tuesday": "8:00-18:00"}',
    'No loud conversations. Keep the desks clean.'
),
(
    'BUS002', 'OFF002', 'Suburban Office', 'A quiet office in the suburbs.', 'private', 1, 20, 40,
    ARRAY['Parking', 'WiFi', 'Tea'],
    '{"monday": "9:00-17:00", "wednesday": "9:00-17:00"}',
    'No pets. Maintain silence.'
);


