package repository.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeAddressRepoFragments {

  val resetOfficeAddressTable: fragment.Fragment = {
    sql"TRUNCATE TABLE office_address RESTART IDENTITY"
  }

  val createOfficeAddressTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS office_address (
        id BIGSERIAL PRIMARY KEY,
        business_id VARCHAR(255),
        office_id VARCHAR(255),
        building_name VARCHAR(255),
        floor_number VARCHAR(50),
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


  val insertOfficeAddressTable: fragment.Fragment = {
    sql"""
        INSERT INTO office_address (
          business_id,
          office_id,
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
          ('business_id_1', 'office_id_1', 'Empire State Building', '5th Floor', '123 Main Street', 'New York', 'USA', 'Manhattan', '10001', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('business_id_2', 'office_id_2', 'Empire State Building', '5th Floor', '123 Main Street', 'New York', 'USA', 'Manhattan', '10001', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('business_id_3', 'office_id_3', 'Empire State Building', '5th Floor', '123 Main Street', 'New York', 'USA', 'Manhattan', '10001', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('business_id_4', 'office_id_4', 'Empire State Building', '5th Floor', '123 Main Street', 'New York', 'USA', 'Manhattan', '10001', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
          ('business_id_5', 'office_id_5', 'Empire State Building', '5th Floor', '123 Main Street', 'New York', 'USA', 'Manhattan', '10001', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }
}
