package models.bookings.errors

sealed trait ValidationError

case object InvalidBookingId extends ValidationError

case object InvalidTimeRange extends ValidationError

case object BookingNotFound extends ValidationError

case object OverlappingBooking extends ValidationError

