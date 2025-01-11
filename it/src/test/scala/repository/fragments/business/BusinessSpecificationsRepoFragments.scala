package repository.fragments.business

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
        user_id VARCHAR(255) NOT NULL,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255) NOT NULL,
        description TEXT,
        availability JSONB,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertBusinessSpecificationsData: fragment.Fragment = {
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
      ('USER001', 'BUS001', 'business_name_1', 'some desc1', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER002', 'BUS002', 'business_name_2', 'some desc2', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER003', 'BUS003', 'business_name_3', 'some desc3', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER004', 'BUS004', 'business_name_4', 'some desc4', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER005', 'BUS005', 'business_name_5', 'some desc5', '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }

}
