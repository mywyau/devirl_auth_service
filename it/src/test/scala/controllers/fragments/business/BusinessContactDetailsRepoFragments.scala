package controllers.fragments.business

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
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255),
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

  val insertBusinessContactDetailsTable: fragment.Fragment = {
    sql"""
      INSERT INTO business_contact_details (
        user_id,
        business_id,
        business_name,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        website_url,
        created_at,
        updated_at
      ) VALUES
       ('user_id_1','business_id_1','Example Business Name','John','Doe','johndoe@example.com','123-456-7890','https://example.com','2025-01-01 00:00:00','2025-01-01 00:00:00'),
       ('user_id_2','business_id_2','Example Business Name','John','Doe','johndoe@example.com','123-456-7890','https://example.com','2025-01-01 00:00:00','2025-01-01 00:00:00');
     """
  }
}
