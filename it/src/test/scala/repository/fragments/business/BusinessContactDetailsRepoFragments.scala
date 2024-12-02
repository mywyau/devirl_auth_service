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
        user_id VARCHAR(255) NOT NULL UNIQUE,
        business_id VARCHAR(255) NOT NULL UNIQUE,
        business_name VARCHAR(255),
        primary_contact VARCHAR(255),
        contact_email VARCHAR(255),
        contact_phone VARCHAR(20),
        website_url VARCHAR(255),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }
}
