package repositories.bookings

import cats.effect.Concurrent
import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.*
import models.bookings.{Booking, BookingStatus}

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
      VALUES (${booking.bookingId}, ${booking.bookingName}, ${booking.userId}, ${booking.workspaceId}, ${booking.bookingDate}, ${booking.startTime}, ${booking.endTime}, ${booking.status}, ${booking.createdAt})
    """.update
      .run
      .transact(transactor)
  }


  def updateBooking(bookingId: String, updatedBooking: Booking): F[Int] = {
    sql"""
      UPDATE bookings
      SET booking_name = ${updatedBooking.bookingName},
          user_id = ${updatedBooking.userId},
          workspace_id = ${updatedBooking.workspaceId},
          booking_date = ${updatedBooking.bookingDate},
          start_time = ${updatedBooking.startTime},
          end_time = ${updatedBooking.endTime},
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
    WHERE workspace_id = ${booking.workspaceId}
      AND booking_date = ${booking.bookingDate}
      AND (
        (startTime::TIME, end_time::TIME)
        OVERLAPS
        (${booking.startTime}::TIME, ${booking.endTime}::TIME)
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
        VALUES (${booking.bookingId}, ${booking.bookingName}, ${booking.userId}, ${booking.workspaceId}, ${booking.bookingDate}, ${booking.startTime}, ${booking.endTime}, ${booking.status}, ${booking.createdAt})
      """.update.run.transact(transactor).map(Right(_))
      }
    }
  }

}
