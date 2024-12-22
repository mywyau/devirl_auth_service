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

  val insertOfficeContactDetailsData = {
    sql"""
      INSERT INTO office_contact_details (
        business_id,
        office_id,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        created_at,
        updated_at
      ) VALUES
        ('BUS12345', 'OFF001', 'Alice', 'Johnson', 'alice.johnson@example.com', '+15551234567', '2023-01-01 12:00:00', '2023-01-01 12:00:00'),
        ('BUS67890', 'OFF002', 'Bob', 'Smith', 'bob.smith@example.com', '+15557654321', '2023-02-01 15:30:00', '2023-02-01 15:30:00'),
        ('BUS11223', 'OFF003', 'Carol', 'Davis', 'carol.davis@example.com', '+15559876543', '2023-03-01 09:45:00', '2023-03-01 09:45:00');
    """
  }
}
