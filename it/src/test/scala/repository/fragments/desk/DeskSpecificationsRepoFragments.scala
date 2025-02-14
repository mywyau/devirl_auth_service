package repository.fragments.desk

import doobie.implicits._
import doobie.util.fragment

object DeskSpecificationsRepoFragments {

  val resetDeskSpecificationsTable: fragment.Fragment =
    sql"TRUNCATE TABLE desk_specifications RESTART IDENTITY"

  val createDeskSpecificationsTable: fragment.Fragment =
    sql"""
      CREATE TABLE IF NOT EXISTS desk_specifications (
        id SERIAL PRIMARY KEY,
        user_id VARCHAR(255),
        business_id VARCHAR(255),
        office_id VARCHAR(255),
        desk_id VARCHAR(255) UNIQUE,
        desk_name VARCHAR(50),
        description TEXT,
        desk_type VARCHAR(100),
        quantity INT CHECK (quantity >= 0),
        features TEXT[],
        opening_hours JSONB,
        rules TEXT,
        created_at TIMESTAMP DEFAULT '2025-01-01 00:00:00',
        updated_at TIMESTAMP DEFAULT '2025-01-01 00:00:00'
      );
    """

  val insertDeskSpecifications: fragment.Fragment =
    sql"""
      INSERT INTO desk_specifications (
        user_id, business_id, office_id, desk_id, desk_name, description, desk_type,
        quantity, features, opening_hours, rules, created_at, updated_at
      ) VALUES
        (
          'userId1',
          'businessId1',
          'officeId1',
          'deskId1',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          5,
          ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Desk Lamp'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Please keep the desk clean and quiet.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId2',
          'businessId1',
          'officeId2',
          'deskId2',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          3,
          ARRAY['Wi-Fi', 'Power Outlets', 'Whiteboard', 'Projector'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Respect others'' privacy and keep noise levels to a minimum.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId3',
          'businessId2',
          'officeId3',
          'deskId3',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          2,
          ARRAY['Wi-Fi', 'Power Outlets', 'Storage Space', 'View'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Please keep your personal items to a minimum and respect shared spaces.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId4',
          'businessId3',
          'officeId4',
          'deskId4',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          10,
          ARRAY['Wi-Fi', 'Power Outlets', 'Standing Desk'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Please clean up after use and respect the shared space.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId5',
          'businessId4',
          'officeId5',
          'deskId5',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          1,
          ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Storage Space'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No food or drinks allowed at the desk. Keep the workspace organized.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """

  val insertDeskSpecificationsSameOffice: fragment.Fragment =
    sql"""
      INSERT INTO desk_specifications (
        user_id, business_id, office_id, desk_id, desk_name, description, desk_type,
        quantity, features, opening_hours, rules, created_at, updated_at
      ) VALUES
        (
          'userId1',
          'bizId1',
          'officeId1',
          'deskId1',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          5,
          ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Desk Lamp'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No loud conversations, please keep the workspace clean.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user002',
          'bizId1',
          'officeId1',
          'deskId2',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          3,
          ARRAY['Wi-Fi', 'Power Outlets', 'Whiteboard', 'Projector'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Respect others'' privacy and keep noise levels to a minimum.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user003',
          'biz002',
          'officeId1',
          'deskId3',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          2,
          ARRAY['Wi-Fi', 'Power Outlets', 'Storage Space', 'View'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Please keep your personal items to a minimum and respect shared spaces.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user004',
          'biz003',
          'officeId1',
          'deskId4',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          10,
          ARRAY['Wi-Fi', 'Power Outlets', 'Standing Desk'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'Please clean up after use and respect the shared space.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user005',
          'biz004',
          'officeId1',
          'deskId5',
          'Luxury supreme desk',
          'Some description',
          'PrivateDesk',
          1,
          ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Storage Space'],
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          'No food or drinks allowed at the desk. Keep the workspace organized.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """
}
