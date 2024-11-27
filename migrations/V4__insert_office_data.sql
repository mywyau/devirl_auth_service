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
    street,
    city,
    country,
    county,
    postcode,
    building_name,
    floor_number,
    latitude,
    longitude,
    created_at,
    updated_at
) VALUES (
    'business123',                      -- user_id
    'office456',                    -- office_id
    'Empire State Building',        -- building_name
    '5th Floor',                    -- floor_number
    '123 Main Street',              -- street
    'New York',                     -- city
    'USA',                          -- country
    'Manhattan',                    -- county
    '10001',                        -- postcode
    40.748817,                      -- latitude
    -73.985428,                     -- longitude
    CURRENT_TIMESTAMP,              -- created_at
    CURRENT_TIMESTAMP               -- updated_at
);


