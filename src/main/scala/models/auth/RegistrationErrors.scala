package models.auth

sealed trait RegistrationErrors {
   val errorMessage: String
}

sealed trait RegisterPasswordErrors extends RegistrationErrors

case object PasswordLengthError extends RegisterPasswordErrors {
  override val errorMessage = "Password must be between 8 and 20 characters"
}

case object PasswordNoUppercase extends RegisterPasswordErrors {
  override val errorMessage = "Password must contain at least one uppercase letter"
}

case object PasswordNoDigit extends RegisterPasswordErrors {
  override val errorMessage = "Password must contain at least one digit."
}

case object PasswordNoSpecialCharacters extends RegisterPasswordErrors {
  override val errorMessage = "Password must contain at least one special character"
}

case object PasswordContainsWhitespace extends RegisterPasswordErrors {
  override val errorMessage = "Password must not contain whitespace"
}

sealed trait RegisterEmailErrors extends RegistrationErrors

case object EmailAlreadyExists extends RegisterEmailErrors {
  override val errorMessage = "Email already exists"
}

sealed trait RegisterUsernameErrors extends RegistrationErrors

case object UsernameAlreadyExists extends RegisterUsernameErrors {
  override val errorMessage = "User already exists"
}

case object CannotCreateUser extends RegistrationErrors {
  override val errorMessage = "User was not created"
}