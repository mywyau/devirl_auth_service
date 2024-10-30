package services.auth.constants

import cats.data.Validated
import cats.effect.IO
import models.users.*
import repositories.UserRepositoryAlgebra
import services.PasswordServiceAlgebra
import services.auth.constants.RegistrationServiceConstants.*

import java.time.LocalDateTime

object RegistrationServiceConstants {

  val existingUser: User = {
    User(
      userId = "user_id_1",
      username = "existinguser",
      password_hash = "hashedpassword",
      first_name = "First",
      last_name = "Last",
      contact_number = "1234567890",
      email = "existing@example.com",
      role = Wanderer,
      created_at = LocalDateTime.now()
    )
  }
  
  // Sample data
  val validRequest: UserRegistrationRequest = {
    UserRegistrationRequest(
      userId = "user_id_2",
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      contact_number = "1234567890",
      email = "newuser@example.com",
      role = Wanderer
    )
  }

  val uniqueRequest: UserRegistrationRequest = {
    UserRegistrationRequest(
      userId = "user_id_3",
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      contact_number = "0987654321",
      email = "newuser@example.com",
      role = Wanderer
    )
  }

}
