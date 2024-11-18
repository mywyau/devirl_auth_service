package controllers.fragments

import doobie.implicits.*
import doobie.util.fragment

object LoginControllerFragments {

  val resetUserLoginDetailsTable: fragment.Fragment = sql"TRUNCATE TABLE user_login_details RESTART IDENTITY"

  val createUserLoginDetailsTable: fragment.Fragment =
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
          )
    """

  val insertWandererLoginDetailsTable: fragment.Fragment = {
    sql"""
      INSERT INTO user_login_details (user_id, username, password_hash, email, role, created_at, updated_at)
      VALUES('fake_user_id_1', 'mikey5922', 'hashed_password', 'fake_user_1@example.com', 'Wanderer', '2025-01-01 00:00:00', '2025-01-01 00:00:00');
    """
  }
}
