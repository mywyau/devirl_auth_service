package controllers.fragments

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
        business_id VARCHAR(255) NOT NULL,
        office_id VARCHAR(255) NOT NULL UNIQUE,
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


  val insertOfficeAddressesTable: fragment.Fragment = {
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
        ('BUS123', 'OFF001', 'Empire State Building', '5th Floor', 'Main street 123', 'New York', 'USA', 'Manhattan', '123456', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS456', 'OFF002', 'One World Trade Center', '15th Floor', '200 Greenwich Street', 'New York', 'USA', 'Manhattan', '10007', 40.712742, -74.013382, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS789', 'OFF003', 'Chrysler Building', '10th Floor', '405 Lexington Avenue', 'New York', 'USA', 'Manhattan', '10174', 40.751652, -73.975311, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }


  val insertSameBusinessIdOfficeAddressData: fragment.Fragment = {
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
        ('BUS123', 'OFF001', 'Empire State Building', '5th Floor', 'Main street 123', 'New York', 'USA', 'Manhattan', '123456', 40.748817, -73.985428, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS123', 'OFF002', 'One World Trade Center', '15th Floor', '200 Greenwich Street', 'New York', 'USA', 'Manhattan', '10007', 40.712742, -74.013382, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS123', 'OFF003', 'Chrysler Building 1', '10th Floor', '405 Lexington Avenue', 'New York', 'USA', 'Manhattan', '10174', 40.751652, -73.975311, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS123', 'OFF004', 'Chrysler Building 2', '11th Floor', '405 Lexington Avenue', 'New York', 'USA', 'Manhattan', '10174', 40.751652, -73.975311, '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
        ('BUS123', 'OFF005', 'Chrysler Building 3', '12th Floor', '405 Lexington Avenue', 'New York', 'USA', 'Manhattan', '10174', 40.751652, -73.975311, '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }

}
