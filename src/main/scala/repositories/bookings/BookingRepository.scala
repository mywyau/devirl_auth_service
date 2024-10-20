package repositories

import cats.effect.Concurrent
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.meta.Meta
import models._
import models.bookings.BookingStatus

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

trait BookingRepositoryAlgebra[F[_]] {

  def findBookingById(bookingId: String): F[Option[Booking]]

  def getAllBookings: F[List[Booking]]

  def setBooking(booking: Booking): F[Int]

  def updateBooking(bookingId: String, updatedBooking: Booking): F[Int]

  def deleteBooking(bookingId: String): F[Int]

  def doesOverlap(booking: Booking): F[Boolean]
}

class BookingRepository[F[_] : Concurrent](transactor: Transactor[F]) extends BookingRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  // Meta instance to map between LocalDate
  implicit val localDateMeta: Meta[LocalDate] =
    Meta[Date].imap(_.toLocalDate)(Date.valueOf)

  implicit val bookingStatusMeta: Meta[BookingStatus] = Meta[String].imap(BookingStatus.fromString)(_.toString)

  def findBookingById(bookingId: String): F[Option[Booking]] = {
    sql"SELECT * FROM bookings WHERE booking_id = $bookingId"
      .query[Booking]
      .option
      .transact(transactor)
  }

  def findBookingByBookingName(bookingName: String): F[Option[Booking]] = {

    sql"SELECT * FROM bookings WHERE booking_name = $bookingName"
      .query[Booking]
      .option
      .transact(transactor)
  }


  def getAllBookings: F[List[Booking]] = {
    sql"SELECT * FROM bookings"
      .query[Booking]
      .to[List]
      .transact(transactor)
  }

  def setBooking(booking: Booking): F[Int] = {
    sql"""
      INSERT INTO bookings (booking_id, booking_name, user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
      VALUES (${booking.booking_id}, ${booking.booking_name}, ${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})
    """.update
      .run
      .transact(transactor)
  }


  def updateBooking(bookingId: String, updatedBooking: Booking): F[Int] = {
    sql"""
      UPDATE bookings
      SET booking_name = ${updatedBooking.booking_name},
          user_id = ${updatedBooking.user_id},
          workspace_id = ${updatedBooking.workspace_id},
          booking_date = ${updatedBooking.booking_date},
          start_time = ${updatedBooking.start_time},
          end_time = ${updatedBooking.end_time},
          status = ${updatedBooking.status}
      WHERE booking_id = $bookingId
  """.update
      .run
      .transact(transactor)
  }

  def deleteBooking(bookingId: String): F[Int] = {
    sql"""
      DELETE FROM bookings WHERE booking_id = $bookingId
    """.update
      .run
      .transact(transactor)
  }

  def doesOverlap(booking: Booking): F[Boolean] = {
    sql"""
    SELECT COUNT(*) FROM bookings
    WHERE workspace_id = ${booking.workspace_id}
      AND booking_date = ${booking.booking_date}
      AND (
        (start_time::TIME, end_time::TIME)
        OVERLAPS
        (${booking.start_time}::TIME, ${booking.end_time}::TIME)
      )
  """.query[Int].unique.transact(transactor).map(_ > 0)
  }


  def setBookingsWithOverlapCheck(booking: Booking): F[Either[String, Int]] = {
    doesOverlap(booking).flatMap { overlap =>
      if (overlap) {
        Concurrent[F].pure(Left("Booking overlaps with an existing booking"))
      } else {
        sql"""
        INSERT INTO bookings (booking_id, booking_name, user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
        VALUES (${booking.booking_id}, ${booking.booking_name}, ${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})
      """.update.run.transact(transactor).map(Right(_))
      }
    }
  }

}
