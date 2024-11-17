package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object WandererProfileSqlFragments {

  val resetWandererLoginDetailsTable = sql"TRUNCATE TABLE user_login_details RESTART IDENTITY"

  val createWandererLoginDetailsTable: fragment.Fragment = {
    sql"""
        CREATE TABLE IF NOT EXISTS user_login_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          username VARCHAR(255) NOT NULL,
          password_hash TEXT NOT NULL,
          email VARCHAR(255) NOT NULL,
          role VARCHAR(50) NOT NULL,
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        );
    """
  }

  val insertWandererLoginDetailsTable: fragment.Fragment = {
    sql"""
      INSERT INTO user_login_details (user_id, username, password_hash, email, role, created_at, updated_at)
      VALUES('fake_user_id_1', 'fake_username', 'hashed_password', 'fake_user_1@example.com', 'Wanderer', '2023-01-01 12:00:00', '2023-01-01 12:00:00');
    """
  }

  val resetWandererAddressTable = sql"TRUNCATE TABLE wanderer_address RESTART IDENTITY"

  val createWandererAddressTable: fragment.Fragment = {
    sql"""
          CREATE TABLE IF NOT EXISTS wanderer_address (
             id BIGSERIAL PRIMARY KEY,
             user_id VARCHAR(255) NOT NULL,
             street VARCHAR(255),
             city VARCHAR(255),
             country VARCHAR(255),
             county VARCHAR(255),
             postcode VARCHAR(255),
             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
          );
    """
  }

  val insertWandererAddressData: fragment.Fragment = {
    sql"""
          INSERT INTO wanderer_address (
            user_id,
            street,
            city,
            country,
            county,
            postcode,
            created_at,
            updated_at
          ) VALUES (
            'fake_user_id_1',
            '123 Example Street',
            'Sample City',
            'United Kingdom',
            'South Glamorgan',
            'CF5 3NJ',
            '2025-01-01 00:00:00',
            '2025-01-01 00:00:00'
          );
      """
  }

  val resetWandererPersonalDetailsTable = sql"TRUNCATE TABLE wanderer_personal_details RESTART IDENTITY"

  val createWandererPersonalDetailsTable: fragment.Fragment = {
    sql"""
      CREATE TABLE IF NOT EXISTS wanderer_personal_details (
          id BIGSERIAL PRIMARY KEY,
          user_id VARCHAR(255) NOT NULL,
          first_name VARCHAR(255),
          last_name VARCHAR(255),
          contact_number VARCHAR(100),
          email VARCHAR(255),
          company VARCHAR(255),
          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
      );
    """
  }

  val insertWandererPersonalDetailsData: fragment.Fragment = {
    sql"""
      INSERT INTO wanderer_personal_details (user_id, first_name, last_name, contact_number, email, company, created_at, updated_at)
      VALUES('fake_user_id_1', 'bob', 'smith', '0123456789', 'fake_user_1@example.com', 'apple', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }
}
