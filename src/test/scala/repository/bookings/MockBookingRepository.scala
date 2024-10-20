package repository

import cats.effect.IO
import models._
import repositories.BookingRepositoryAlgebra

class MockBookingRepository extends BookingRepositoryAlgebra[IO] {

  private var bookings: Map[String, Booking] = Map.empty

  def withInitialBookings(initial: Map[String, Booking]): MockBookingRepository = {
    val repository = new MockBookingRepository
    repository.bookings = initial
    repository
  }

  override def getAllBookings: IO[List[Booking]] =
    IO.pure(bookings.values.toList)

  override def findBookingById(bookingId: String): IO[Option[Booking]] = {
    IO.pure(bookings.get(bookingId))
  }

  override def setBooking(booking: Booking): IO[Int] = {
    bookings += (booking.booking_id -> booking)
    IO.pure(1)
  }

  override def updateBooking(bookingId: String, updatedBooking: Booking): IO[Int] = {
    if (bookings.contains(bookingId)) {
      bookings += (bookingId -> updatedBooking)
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  override def deleteBooking(bookingId: String): IO[Int] = {
    if (bookings.contains(bookingId)) {
      bookings -= bookingId
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  override def doesOverlap(booking: Booking): IO[Boolean] =
    IO.pure(false)
}
