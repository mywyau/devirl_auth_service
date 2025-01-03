package controllers.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessSpecificationsRepoFragments {

  val resetBusinessSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_specifications RESTART IDENTITY"
  }

  val createBusinessSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_specifications (
        id SERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255) NOT NULL,
        description TEXT,
        availability JSONB,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }

  val insertBusinessSpecsTable: fragment.Fragment = {
    sql"""
      INSERT INTO business_specifications (
        user_id,
        business_id,
        business_name,
        description,
        availability,
        created_at,
        updated_at
      ) VALUES
      ('user_id_1','business_id_1','Example Business Name','some description', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00','2025-01-01 00:00:00'),
      ('user_id_2','business_id_2','Example Business Name','some description', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00','2025-01-01 00:00:00');
     """
  }


}
