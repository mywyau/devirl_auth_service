package repository.fragments.desk

import doobie.implicits.*
import doobie.util.fragment

object DeskPricingRepoFragments {

  val resetDeskPricingsTable: fragment.Fragment =
    sql"TRUNCATE TABLE desk_pricing RESTART IDENTITY"

  val createDeskPricingsTable: fragment.Fragment =
    sql"""
        CREATE TABLE IF NOT EXISTS desk_pricing (
          id SERIAL PRIMARY KEY,
          user_id VARCHAR(255),
          business_id VARCHAR(255),
          office_id VARCHAR(255),
          desk_id VARCHAR(255) UNIQUE,
          price_per_hour DECIMAL(10, 2) CHECK (price_per_hour >= 0),
          price_per_day DECIMAL(10, 2) CHECK (price_per_day >= 0),
          price_per_week DECIMAL(10, 2) CHECK (price_per_week >= 0),
          price_per_month DECIMAL(10, 2) CHECK (price_per_month >= 0),
          price_per_year DECIMAL(10, 2) CHECK (price_per_year >= 0),
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """

  val insertDeskPricings: fragment.Fragment =
    sql"""
      INSERT INTO desk_pricing (
        user_id,
        business_id,
        office_id,
        desk_id,
        price_per_hour,
        price_per_day,
        price_per_week,
        price_per_month,
        price_per_year,
        created_at,
        updated_at
      ) VALUES
        (
          'user001',
          'business001',
          'office001',
          'desk001',
          15.00,
          100.00,
          600.00,
          2000.00,
          24000.00,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
        ),
        (
          'user002',
          'business002',
          'office002',
          'desk002',
          10.00,
          80.00,
          NULL, -- No weekly price
          1500.00,
          18000.00,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
        ),
        (
          'user003',
          'business003',
          'office003',
          'desk003',
          0.00, -- Defaulting to 0 for no hourly price
          120.00,
          700.00,
          2500.00,
          0.00, -- Defaulting to 0 for no yearly price
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
        ),
        (
          'user004',
          'business004',
          'office004',
          'desk004',
          20.00,
          0.00, -- Defaulting to 0 for no daily price
          1000.00,
          3000.00,
          36000.00,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
        ),
        (
          'user005',
          'business005',
          'office005',
          'desk005',
          0.00, -- Defaulting to 0 for no hourly price
          0.00, -- Defaulting to 0 for no daily price
          0.00, -- Defaulting to 0 for no weekly price
          0.00, -- Defaulting to 0 for no monthly price
          50000.00,
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
        );
    """
}
