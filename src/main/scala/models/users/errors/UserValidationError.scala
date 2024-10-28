package models.users.errors

sealed trait UserValidationError

case object InvalidUserId extends UserValidationError

case object InvalidTimeRange extends UserValidationError

case object UserNotFound extends UserValidationError


