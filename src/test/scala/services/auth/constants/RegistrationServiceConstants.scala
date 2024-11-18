package services.auth.constants

import cats.data.Validated
import cats.effect.IO
import models.users.*
import models.users.adts.Wanderer
import models.users.wanderer_profile.profile.UserLoginDetails
import models.users.wanderer_profile.requests.UserSignUpRequest
import repositories.users.UserProfileRepositoryAlgebra

import java.time.LocalDateTime

object RegistrationServiceConstants {

  val existingUser: UserLoginDetails = {
    UserLoginDetails(
      id = Some(1),
      userId = "user_id_1",
      username = "existinguser",
      passwordHash = "hashedpassword",
      email = "existing@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  val validRequest: UserSignUpRequest = {
    UserSignUpRequest(
      userId = "user_id_2",
      username = "newuser",
      password = "ValidPass123!",
      email = "newuser@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  val uniqueRequest: UserSignUpRequest = {
    UserSignUpRequest(
      userId = "user_id_3",
      username = "newuser",
      password = "ValidPass123!",
      email = "newuser@example.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
