package repository.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeSpecsRepoFragments {

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
}
