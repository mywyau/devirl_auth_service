package controllers.fragments.desk

import doobie.implicits.*
import doobie.util.fragment

object DeskSpecificationsControllerFragments {

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
        ('user001', 'biz001', 'officeId1', 'deskId1', 'Mikey Desk 1', 
         'A quiet, private desk perfect for focused work with a comfortable chair and good lighting.', 'PrivateDesk', 
         5, 
         ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Desk Lamp'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'No loud conversations, please keep the workspace clean.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user002', 'biz001', 'office002', 'deskId2', 'Mikey Desk 2', 
         'A shared desk in a collaborative space with easy access to team members.', 'PrivateDesk', 
         3,
         ARRAY['Wi-Fi', 'Power Outlets', 'Whiteboard', 'Projector'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Respect others'' privacy and keep noise levels to a minimum.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user003', 'biz002', 'office003', 'deskId3', 'Mikey Desk 3', 
         'Spacious desk with a view and ample storage for your items.', 'PrivateDesk', 
         2,
         ARRAY['Wi-Fi', 'Power Outlets', 'Storage Space', 'View'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Please keep your personal items to a minimum and respect shared spaces.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user004', 'biz003', 'office004', 'deskId4', 'Mikey Desk 4', 
         'A flexible, hot desk available for use in a dynamic work environment.', 'PrivateDesk', 
         10,
         ARRAY['Wi-Fi', 'Power Outlets', 'Standing Desk'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Please clean up after use and respect the shared space.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user005', 'biz004', 'office005', 'deskId5', 'Mikey Desk 5', 
         'An executive desk in a quiet, well-lit space designed for high-level work.', 'PrivateDesk', 
         NULL, -- Missing quantity
         NULL, -- Missing features
         NULL, -- Missing opening_hours
         NULL, -- Missing rules
         '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """

  val sameOfficeIdInsertDeskSpecifications: fragment.Fragment =
    sql"""
      INSERT INTO desk_specifications (
        user_id, business_id, office_id, desk_id, desk_name, description, desk_type, 
        quantity, features, opening_hours, rules, created_at, updated_at
      ) VALUES
        ('user001', 'biz001', 'office01', 'deskId1', 'Mikey Desk 1', 
         'A quiet, private desk perfect for focused work with a comfortable chair and good lighting.', 'PrivateDesk', 
         5,
         ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Desk Lamp'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'No loud conversations, please keep the workspace clean.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user002', 'biz001', 'office01', 'deskId2', 'Mikey Desk 2', 
         'A shared desk in a collaborative space with easy access to team members.', 'PrivateDesk', 
         3, 
         ARRAY['Wi-Fi', 'Power Outlets', 'Whiteboard', 'Projector'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Respect others'' privacy and keep noise levels to a minimum.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user003', 'biz002', 'office01', 'deskId3', 'Mikey Desk 3', 
         'Spacious desk with a view and ample storage for your items.', 'PrivateDesk', 
         2, 
         ARRAY['Wi-Fi', 'Power Outlets', 'Storage Space', 'View'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Please keep your personal items to a minimum and respect shared spaces.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user004', 'biz003', 'office01', 'deskId4', 'Mikey Desk 4', 
         'A flexible, hot desk available for use in a dynamic work environment.', 'PrivateDesk', 
         10,
         ARRAY['Wi-Fi', 'Power Outlets', 'Standing Desk'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Please clean up after use and respect the shared space.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('user005', 'biz004', 'office01', 'deskId5', 'Mikey Desk 5', 
         'An executive desk in a quiet, well-lit space designed for high-level work.', 'PrivateDesk', 
         1, 
         ARRAY['Wi-Fi', 'Power Outlets', 'Ergonomic Chair', 'Storage Space'], 
        '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'No food or drinks allowed at the desk. Keep the workspace organized.', 
         '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
}
