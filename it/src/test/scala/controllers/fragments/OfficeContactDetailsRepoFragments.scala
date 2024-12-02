package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object OfficeContactDetailsRepoFragments {

  val resetOfficeContactDetailsTable: fragment.Fragment = {
    sql"TRUNCATE TABLE office_contact_details RESTART IDENTITY"
  }

  val createOfficeContactDetailsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS office_contact_details (
        id BIGSERIAL PRIMARY KEY,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        office_id VARCHAR(255) NOT NULL UNIQUE,
        primary_contact_first_name VARCHAR(255),
        primary_contact_last_name VARCHAR(255),
        contact_email VARCHAR(255),
        contact_number VARCHAR(20),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }
}
