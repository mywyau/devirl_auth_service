package models.business.errors

sealed trait BusinessValidationError

case object InvalidBusinessId extends BusinessValidationError

case object InvalidTimeRange extends BusinessValidationError

case object BusinessNotFound extends BusinessValidationError


