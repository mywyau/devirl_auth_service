package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeSpecificationRepoFragments {

  val resetOfficeSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE office_specifications RESTART IDENTITY"
  }

  val createOfficeSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS office_specifications (
        id SERIAL PRIMARY KEY,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        office_id VARCHAR(255) NOT NULL UNIQUE,
        office_name VARCHAR(255),
        description TEXT,
        office_type VARCHAR(100),
        number_of_floors INT,
        total_desks INT,
        capacity INT,
        amenities TEXT[],
        availability JSONB,
        rules TEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertOfficeSpecificationsTable: fragment.Fragment = {
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
          availability,
          rules,
          created_at,
          updated_at
      ) VALUES
      (
          'BUS001',
          'OFF001',
          'Downtown Workspace',
          'A modern co-working space located in the heart of downtown.',
          'PrivateOffice',
          2,
          50,
          100,
          ARRAY['Wi-Fi', 'Coffee Machine', 'Meeting Rooms'],
          '{"days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"], "startTime": "08:00:00", "endTime": "18:00:00"}',
          'No loud conversations. Keep the desks clean.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
      ),
      (
          'BUS002',
          'OFF002',
          'Suburban Office',
          'A quiet office in the suburbs, perfect for focused work.',
          'PrivateOffice',
          1,
          20,
          40,
          ARRAY['Wi-Fi', 'Tea', 'Parking'],
          '{"days": ["Monday", "Wednesday"], "startTime": "08:00:00", "endTime": "18:00:00"}',
          'No pets. Maintain silence.',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
      );
    """
  }

}
