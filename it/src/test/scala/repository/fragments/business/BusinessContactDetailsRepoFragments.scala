package repository.fragments.business

import doobie.implicits.*
import doobie.util.fragment

object BusinessContactDetailsRepoFragments {

  val resetBusinessContactDetailsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE business_contact_details RESTART IDENTITY"
  }

  val createBusinessContactDetailsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS business_contact_details (
        id BIGSERIAL PRIMARY KEY,
        user_id VARCHAR(255) NOT NULL,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        primary_contact_first_name VARCHAR(255),
        primary_contact_last_name VARCHAR(255),
        contact_email VARCHAR(255),
        contact_number VARCHAR(20),
        website_url VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }


  val insertBusinessContactDetailsData: fragment.Fragment = {
    sql"""
      INSERT INTO business_contact_details (
        user_id,
        business_id,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        website_url,
        created_at,
        updated_at
      ) VALUES
      ('USER001', 'BUS001', 'Bob1', 'Smith', 'bob1@gmail.com', '07402205071', 'bobs_axes.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER002', 'BUS002', 'Bob2', 'Smith', 'bob2@gmail.com', '07402205071', 'bobs_axes.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER003', 'BUS003', 'Bob3', 'Smith', 'bob3@gmail.com', '07402205071', 'bobs_axes.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER004', 'BUS004', 'Bob4', 'Smith', 'bob4@gmail.com', '07402205071', 'bobs_axes.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00'),
      ('USER005', 'BUS005', 'Bob5', 'Smith', 'bob5@gmail.com', '07402205071', 'bobs_axes.com', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }
}
