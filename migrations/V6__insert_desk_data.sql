INSERT INTO desk_listings (
    business_id,
    workspace_id,
    title,
    description,
    desk_type,
    quantity,
    price_per_hour,
    price_per_day,
    features,
    availability,
    rules,
    created_at,
    updated_at
)
VALUES
-- Desk 1: Private Desk
('biz001', 'ws001', 'Private Desk', 'A quiet private desk for focused work.', 'PrivateDesk',
 5, 15.50, 80.00,
 ARRAY['Wi-Fi', 'Power Outlets', 'Monitor'],
 '{"days": ["Monday", "Tuesday", "Wednesday"], "startTime": "2023-01-01 12:05:00", "endTime": "2023-01-01 13:05:00"}',
 'No loud conversations. Keep the desk clean.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Desk 2: Hot Desk
('biz002', 'ws002', 'Hot Desk', 'A shared hot desk in a collaborative workspace.', 'HotDesk',
 10, 10.00, 50.00,
 ARRAY['Wi-Fi', 'Coffee', 'Desk Lamp'],
 '{"days": ["Thursday", "Friday"], "startTime": "2023-01-01 12:05:00", "endTime": "2023-01-01 13:05:00"}',
 'First come, first served. Clean up after use.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Desk 3: Executive Desk
('biz003', 'ws003', 'Executive Desk', 'A luxurious executive desk with premium amenities.', 'ExecutiveDesk',
 1, 50.00, 250.00,
 ARRAY['Wi-Fi', 'Power Outlets', 'Monitor', 'Coffee'],
 '{"days": ["Monday", "Tuesday"], "startTime": "2023-01-01 12:05:00", "endTime": "2023-01-01 13:05:00"}',
 'Reserved for executives. Maintain decorum.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Desk 4: Standing Desk
('biz004', 'ws004', 'Standing Desk', 'An ergonomic standing desk with adjustable height.', 'StandingDesk',
 3, 12.00, 60.00,
 ARRAY['Wi-Fi', 'Adjustable Height', 'Monitor'],
 '{"days": ["Wednesday", "Thursday", "Friday"], "startTime": "2023-01-01 12:05:00", "endTime": "2023-01-01 13:05:00"}',
 'Do not move the desk without permission.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Desk 5: Outdoor Desk
('biz005', 'ws005', 'Outdoor Desk', 'A desk located outdoors for a natural working environment.', 'OutdoorDesk',
 2, 20.00, 100.00,
 ARRAY['Wi-Fi', 'Shade Umbrella', 'Power Outlets'],
 '{"days": ["Saturday", "Sunday"], "startTime": "2023-01-01 12:05:00", "endTime": "2023-01-01 13:05:00"}',
 'Protect devices from weather conditions.',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);


