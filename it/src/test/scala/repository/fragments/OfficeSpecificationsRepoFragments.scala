package repository.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeSpecificationsRepoFragments {

  val resetOfficeSpecsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE office_specs RESTART IDENTITY"
  }

  val createOfficeSpecsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS office_specs (
        id SERIAL PRIMARY KEY,
        business_id VARCHAR(255),
        office_id VARCHAR(255),
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

  val insertOfficeSpecificationData: doobie.Fragment = {
    sql"""
        INSERT INTO office_specs (
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
          ('BUS001', 'OFF001', 'Main Office', 'Spacious and well-lit office for teams.', 'PrivateOffice', 2, 20, 50, ARRAY['WiFi', 'Parking'], '{"days":["Monday","Friday"],"startTime":"09:00:00","endTime":"17:00:00"}'::jsonb, 'No smoking indoors.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),
          ('BUS002', 'OFF002', 'Satellite Office', 'A compact office for remote teams.', 'SharedOffice', 1, 10, 30, ARRAY['Coffee', 'WiFi'], '{"days":["Tuesday","Thursday"],"startTime":"10:00:00","endTime":"16:00:00"}'::jsonb, 'Keep noise levels low.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),
          ('BUS003', 'OFF003', 'Creative Space', 'Designed for collaboration and brainstorming.', 'CoworkingSpace', 3, 40, 100, ARRAY['Food', 'WiFi', 'Air Conditioning'], '{"days":["Monday","Wednesday","Friday"],"startTime":"08:00:00","endTime":"18:00:00"}'::jsonb, 'Respect shared equipment.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),
          ('BUS004', 'OFF004', 'Corporate Suite', 'Premium office space for executives.', 'PrivateOffice', 5, 10, 20, ARRAY['Parking', 'WiFi', 'Printer'], '{"days":["Monday","Tuesday","Wednesday"],"startTime":"07:00:00","endTime":"19:00:00"}'::jsonb, 'Dress code: Business casual.', '2025-01-01 12:00:00', '2025-01-01 12:00:00'),
          ('BUS005', 'OFF005', 'Startup Hub', 'Affordable office for small startups.', 'CoworkingSpace', 1, 15, 40, ARRAY['WiFi', 'Coffee', 'Food'], '{"days":["Monday","Thursday"],"startTime":"09:30:00","endTime":"17:30:00"}'::jsonb, 'First come, first served seating.', '2025-01-01 12:00:00', '2025-01-01 12:00:00');
      """
  }

}
