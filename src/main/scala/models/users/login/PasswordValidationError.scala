package models.users.login

sealed trait PasswordValidationError {
  val message: String
}

case object UsernameExistsError extends PasswordValidationError {
  val message = "Username already exists"
}

case object ContactNumberExistsError extends PasswordValidationError {
  val message = "Contact number already exists"
}

case object EmailExistsError extends PasswordValidationError {

  val message = "Email already exists"

}

case object UserCreationError extends PasswordValidationError {

  val message = "Failed to create user due to an unknown error"

}

case object InvalidPasswordError extends PasswordValidationError {

  val message = "Password does not meet security requirements"
}
