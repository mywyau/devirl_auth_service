package services.auth.constants

import models.users.{User, UserRegistrationRequest, Wanderer}

import java.time.LocalDateTime

object RegistrationConstants {

  // Sample data
  val validRequest: UserRegistrationRequest = {
    UserRegistrationRequest(
      username = "newuser",
      password = "ValidPass123!",
      first_name = "First",
      last_name = "Last",
      contact_number = "1234567890",
      email = "newuser@example.com",
      role = Wanderer
    )
  }

  val existingUser: User = {
    User(
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

  val uniqueRequest: UserRegistrationRequest = {
    UserRegistrationRequest(
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
