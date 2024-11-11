package services.auth.constants

import models.users.*
import models.users.adts.{Admin, Wanderer}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}

import java.time.LocalDateTime

object AuthenticationServiceConstants {

  val testUserLoginDetails: UserLoginDetails =
    UserLoginDetails(
      id = Some(1),
      user_id = "user_id_1",
      username = "username",
      password_hash = "hashed_password",
      email = "john@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testUserAddress: UserAddress =
    UserAddress(
      userId = "user_id_1",
      street = "fake street 1",
      city = "fake city 1",
      country = "UK",
      county = Some("County 1"),
      postcode = "CF3 3NJ",
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testUserProfile: UserProfile =
    UserProfile(
      userId = "user_id_1",
      userLoginDetails = testUserLoginDetails,
      first_name = "John",
      last_name = "Doe",
      testUserAddress,
      contact_number = "07402205071",
      email = "john@example.com",
      role = Admin,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  
}
