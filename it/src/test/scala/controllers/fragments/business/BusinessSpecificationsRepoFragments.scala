package controllers.fragments.business

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

  val insertBusinessSpecsTable: fragment.Fragment =
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
          'Example Business Name',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user_id_2',
          'business_id_2',
          'Example Business Name',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'user_id_4',
          'business_id_4',
          'Example Business Name',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        );
    """

  val sameUserIdBusinessSpecsData: fragment.Fragment =
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
          'USER123',
          'businessId1',
          'Example Business Name',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'USER123',
          'business_id_2',
          'Example Business Name',
          'some description',
          '[
            { "day": "Monday", "openingTime": "09:00", "closingTime": "17:00" },
            { "day": "Tuesday", "openingTime": "09:00", "closingTime": "17:00" }
          ]',
          '2025-01-01 00:00:00',
          '2025-01-01 00:00:00'
        ),
        (
          'USER123',
          'business_id_3',
          'Example Business Name',
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
