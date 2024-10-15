package repositories

import cats.effect.{Concurrent, Sync}
import cats.implicits._
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.meta.Meta
import models._

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

sealed trait ValidationError

case object InvalidBookingId extends ValidationError

case object InvalidTimeRange extends ValidationError

class BookingRepository[F[_] : Concurrent](transactor: Transactor[F]) {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  // Meta instance to map between LocalDate
  implicit val localDateMeta: Meta[LocalDate] =
    Meta[Date].imap(_.toLocalDate)(Date.valueOf)

  implicit val bookingStatusMeta: Meta[BookingStatus] =
    Meta[String].imap[BookingStatus] {
      case "Pending" => Pending
      case "Confirmed" => Confirmed
      case "Cancelled" => Cancelled
    } {
      case Pending => "Pending"
      case Confirmed => "Confirmed"
      case Cancelled => "Cancelled"
    }

  def findBookingById(bookingId: String): F[Option[Booking]] = {
    sql"SELECT * FROM bookings WHERE id = $bookingId"
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

    // Correct logging to show the actual values (without "Some" in the log)
    //    println(s"VALUES (${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})")

    // Pass deskId and roomId as Option values, so Doobie will handle them
    sql"""
      INSERT INTO bookings (user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
      VALUES (${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})
    """.update
      .run
      .transact(transactor)
  }

  def updateBooking(bookingId: String, updatedBooking: Booking): F[Int] = {
    sql"""
      UPDATE bookings
      SET user_id = ${updatedBooking.user_id},
          workspace_id = ${updatedBooking.workspace_id},
          booking_date = ${updatedBooking.booking_date},
          start_time = ${updatedBooking.start_time},
          end_time = ${updatedBooking.end_time},
          status = ${updatedBooking.status}
      WHERE id = $bookingId
  """.update
      .run
      .transact(transactor)
  }

  def deleteBooking(bookingId: String): F[Int] = {
    sql"""
      DELETE FROM bookings WHERE id = $bookingId
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
        (start_time, end_time) OVERLAPS (${booking.start_time}, ${booking.end_time})
      )
  """.query[Int].unique.transact(transactor).map(_ > 0)
  }

  def setBookingsWithOverlapCheck(booking: Booking): F[Either[String, Int]] = {
    doesOverlap(booking).flatMap { overlap =>
      if (overlap) {
        Concurrent[F].pure(Left("Booking overlaps with an existing booking"))
      } else {
        sql"""
        INSERT INTO bookings (user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
        VALUES (${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})
      """.update.run.transact(transactor).map(Right(_))
      }
    }
  }

}
