package repository.fragments.business

import doobie.implicits._
import doobie.util.fragment

object BusinessSpecificationsRepoFragments {

  val resetBusinessSpecsTable: fragment.Fragment =
    sql"TRUNCATE TABLE business_specifications RESTART IDENTITY"

  val createBusinessSpecsTable: fragment.Fragment =
    sql"""
      CREATE TABLE IF NOT EXISTS business_specifications (
        id SERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255) NOT NULL,
        description TEXT,
        opening_hours JSONB,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """

  val insertBusinessSpecificationsData: fragment.Fragment =
    sql"""
      INSERT INTO business_specifications (
        user_id,
        business_id,
        business_name,
        description,
        opening_hours,
        created_at,
        updated_at
      ) VALUES
        (
          'userId1',
          'businessId1',
          'businessName1',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId2',
          'businessId2',
          'businessName2',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId3',
          'businessId3',
          'businessName3',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId4',
          'businessId4',
          'businessName4',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'userId5',
          'businessId5',
          'businessName5',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """
}
