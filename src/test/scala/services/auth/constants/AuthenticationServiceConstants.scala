package services.auth.constants

import models.users.{Admin, UserAddress, UserLoginDetails, UserProfile}

import java.time.LocalDateTime

object AuthenticationServiceConstants {

  val testUser: UserProfile =
    UserProfile(
      userId = "user_id_1",
      UserLoginDetails(
        userId = "user_id_1",
        username = "username",
        password_hash = "hashed_password",
      ),
      first_name = "John",
      last_name = "Doe",
      UserAddress(
        userId = "user_id_1",
        street = "fake street 1",
        city = "fake city 1",
        country = "UK",
        county = Some("County 1"),
        postcode = "CF3 3NJ",
        created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
      ),
      contact_number ="07402205071",
      email ="john@example.com",
      role = Admin,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val users: Map[String, UserProfile] = Map("username" -> testUser)

}
