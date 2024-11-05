package services.bookings.algebra

import models.Booking
import models.bookings.errors.*


trait BookingServiceAlgebra[F[_]] {

  def findBookingById(bookingId: String): F[Either[ValidationError, Booking]]

  def createBooking(booking: Booking): F[Either[ValidationError, Int]]

  def updateBooking(bookingId: String, updatedBooking: Booking): F[Either[ValidationError, Int]]

  def deleteBooking(bookingId: String): F[Either[ValidationError, Int]]
}
