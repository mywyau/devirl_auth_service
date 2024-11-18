package models.users.registration

sealed trait RegistrationValidation

case object NotUnique extends RegistrationValidation

case object UniqueUser extends RegistrationValidation