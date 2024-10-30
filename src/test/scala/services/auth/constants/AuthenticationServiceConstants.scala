package services.auth.constants

import models.users.{Admin, User}

import java.time.LocalDateTime

object AuthenticationServiceConstants {

  val testUser: User =
    User(
      userId = "user_id_1",
      username = "username",
      password_hash = "hashed_password",
      first_name = "John",
      last_name = "Doe",
      contact_number = "07402205071",
      email = "john@example.com",
      role = Admin,
      created_at = LocalDateTime.now()
    )
    
  val users: Map[String, User] = Map("username" -> testUser)

}
