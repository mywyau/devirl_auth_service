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


