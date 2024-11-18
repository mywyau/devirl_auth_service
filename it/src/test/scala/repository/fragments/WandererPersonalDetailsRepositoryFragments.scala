package repository.fragments

import doobie.implicits.*
import doobie.util.fragment

object WandererPersonalDetailsRepositoryFragments {

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
