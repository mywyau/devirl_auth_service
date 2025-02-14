package repository.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeSpecificationsRepoFragments {

  val resetOfficeSpecsTable: fragment.Fragment =
    sql"TRUNCATE TABLE office_specifications RESTART IDENTITY"

  val createOfficeSpecsTable: fragment.Fragment =
    sql"""
      CREATE TABLE IF NOT EXISTS office_specifications (
        id SERIAL PRIMARY KEY,
        business_id VARCHAR(255),
        office_id VARCHAR(255),
        office_name VARCHAR(255),
        description TEXT,
        office_type VARCHAR(100),
        number_of_floors INT,
        total_desks INT,
        capacity INT,
        amenities TEXT[],
        opening_hours JSONB,
        rules TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """

  val insertOfficeSpecificationData: doobie.Fragment =
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        total_desks,
        capacity,
        amenities,
        opening_hours,
        rules,
        created_at,
        updated_at
      ) VALUES
        ('businessId1', 'officeId1', 'Magnificent Office', 'some office description', 'PrivateOffice', 
         3, 3, 50, ARRAY['WiFi', 'Parking'],
         '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'No smoking indoors.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),

        ('businessId2', 'officeId2', 'Satellite Office', 'some office description', 'SharedOffice', 
         3, 3, 50, ARRAY['Coffee', 'WiFi'],
         '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Keep noise levels low.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),

        ('businessId3', 'officeId3', 'Creative Space', 'some office description', 'CoworkingSpace', 
         3, 3, 50, ARRAY['Food', 'WiFi', 'Air Conditioning'],
         '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Respect shared equipment.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),

        ('businessId4', 'officeId4', 'Corporate Suite', 'some office description', 'PrivateOffice', 
         3, 3, 50, ARRAY['Parking', 'WiFi', 'Printer'],
         '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'Dress code: Business casual.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),

        ('businessId5', 'officeId5', 'Startup Hub', 'some office description', 'CoworkingSpace', 
         3, 3, 50, ARRAY['WiFi', 'Coffee', 'Food'],
         '[
           { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
           { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
         ]',
         'First come, first served seating.', '2025-01-01 12:00:00', '2025-01-01 12:00:00');
    """

  val initiateOfficeSpecificationData: fragment.Fragment =
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        created_at,
        updated_at
      ) VALUES
        ('businessId6', 'officeId6', '2025-01-01 12:00:00', '2025-01-01 12:00:00');
    """

}
