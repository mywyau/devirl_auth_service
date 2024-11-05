package services.auth.constants

import cats.data.Validated
import cats.effect.IO
import models.users.*
import models.users.database.UserLoginDetails
import models.users.requests.UserSignUpRequest
import repositories.users.UserProfileRepositoryAlgebra

import java.time.LocalDateTime

object RegistrationServiceConstants {

  val existingUser: UserLoginDetails = {
    UserLoginDetails(
      id = Some(1),
      user_id = "user_id_1",
      username = "existinguser",
      password_hash = "hashedpassword",
      email = "existing@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  val validRequest: UserSignUpRequest = {
    UserSignUpRequest(
      user_id = "user_id_2",
      username = "newuser",
      password = "ValidPass123!",
      email = "newuser@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

  val uniqueRequest: UserSignUpRequest = {
    UserSignUpRequest(
      user_id = "user_id_3",
      username = "newuser",
      password = "ValidPass123!",
      email = "newuser@example.com",
      role = Wanderer,
      created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
  }

}
