package repository.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessAvailabilityRepoFragments {

  val resetBusinessAvailabilityTable: fragment.Fragment =
    sql"TRUNCATE TABLE business_opening_hours RESTART IDENTITY"

  val createBusinessAvailabilityTable: fragment.Fragment =
    sql"""
      CREATE TABLE IF NOT EXISTS business_opening_hours (
          id BIGSERIAL PRIMARY KEY UNIQUE,
          user_id VARCHAR(255) NOT NULL,
          business_id VARCHAR(255) NOT NULL,
          weekday VARCHAR(10) NOT NULL CHECK (weekday IN 
              ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday')),                            
          opening_time TIME,                            
          closing_time TIME,                            
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          UNIQUE (business_id, weekday) -- Prevents duplicate weekdays per business
      );
    """

  val insertBusinessAvailabilityData: fragment.Fragment =
    sql"""
        INSERT INTO business_opening_hours (
          user_id,
          business_id,
          weekday,
          opening_time,
          closing_time,
          created_at,
          updated_at
        ) VALUES
          ('userId1', 'businessId1', 'Monday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('userId1', 'businessId1', 'Tuesday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('userId1', 'businessId1', 'Wednesday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('userId2', 'businessId2', 'Saturday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('userId3', 'businessId3', 'Saturday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('userId2', 'businessId2', 'Sunday', '09:00:00+00', '17:00:00+01', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
      """

}
