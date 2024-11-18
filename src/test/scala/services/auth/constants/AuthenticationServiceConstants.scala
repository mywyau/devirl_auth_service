package services.auth.constants

import models.users.*
import models.users.adts.{Admin, Wanderer}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}

import java.time.LocalDateTime

object AuthenticationServiceConstants {

  val testUserLoginDetails: UserLoginDetails =
    UserLoginDetails(
      id = Some(1),
      userId = "user_id_1",
      username = "username",
      passwordHash = "hashed_password",
      email = "john@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testUserAddress: UserAddress =
    UserAddress(
      userId = "user_id_1",
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testUserProfile: UserProfile =
    UserProfile(
      userId = "user_id_1",
      userLoginDetails = testUserLoginDetails,
      firstName = "John",
      lastName = "Doe",
      testUserAddress,
      contactNumber = "07402205071",
      email = "john@example.com",
      role = Admin,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  
}
