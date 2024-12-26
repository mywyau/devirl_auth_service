package repository.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessAddressRepoFragments {

  val resetBusinessAddressTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_address RESTART IDENTITY"
  }

  val createBusinessAddressTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_address (
        id BIGSERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255),
        building_name VARCHAR(255),
        floor_number VARCHAR(255),
        street VARCHAR(255),
        city VARCHAR(255),
        country VARCHAR(255),
        county VARCHAR(255),
        postcode VARCHAR(255),
        latitude DECIMAL(9,6),
        longitude DECIMAL(9,6),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertBusinessAddressData: fragment.Fragment = {
    sql"""
        INSERT INTO business_address (
          user_id,
          business_id,
          business_name,
          building_name,
          floor_number,
          street,
          city,
          country,
          county,
          postcode,
          latitude,
          longitude,
          created_at,
          updated_at
        ) VALUES
          ('USER001', 'BUS001', 'Tech Innovations', 'Innovation Tower', '5', '123 Tech Street', 'San Francisco', 'USA', 'California', '94105', 37.774929, -122.419416, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('USER002', 'BUS002', 'Global Corp', 'Global Tower', '12', '456 Global Ave', 'New York', 'USA', 'New York', '10001', 40.712776, -74.005974, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('USER003', 'BUS003', 'Green Solutions', 'Eco Center', '3', '789 Greenway Blvd', 'Austin', 'USA', 'Texas', '73301', 30.267153, -97.743057, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('USER004', 'BUS004', 'Retail Experts', 'Market Plaza', '1', '101 Main Street', 'Chicago', 'USA', 'Illinois', '60601', 41.878113, -87.629799, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('USER005', 'BUS005', 'Startup Central', 'Startup Hub', '2', '202 Startup Lane', 'Seattle', 'USA', 'Washington', '98101', 47.606209, -122.332069, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
      """
  }

}
