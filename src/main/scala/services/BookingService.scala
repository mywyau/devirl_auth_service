package services

import cats._
import cats.effect._
import cats.implicits._
import models._
import repositories._
import java.time.LocalDateTime

trait BookingService[F[_]] {
  def getAvailableDesks: F[List[Desk]]
  def bookDesk(userId: String, deskId: String, startTime: LocalDateTime, endTime: LocalDateTime): F[Either[BookingError, Booking]]
}

sealed trait BookingError extends Throwable
case class DeskNotAvailable(deskId: String) extends BookingError
case class DeskNotFound(deskId: String) extends BookingError

class BookingServiceImpl[F[_]: Monad](repository: BookingRepository[F]) extends BookingService[F] {

  override def getAvailableDesks: F[List[Desk]] = {
    repository.getAvailableDesks
  }

  override def bookDesk(userId: String, deskId: String, startTime: LocalDateTime, endTime: LocalDateTime): F[Either[BookingError, Booking]] = {
    for {
      maybeDesk <- repository.findDeskById(deskId)
      result <- maybeDesk match {
        case None => DeskNotFound(deskId).asLeft[Booking].pure[F]
        case Some(desk) if desk.status != "available" => DeskNotAvailable(deskId).asLeft[Booking].pure[F]
        case Some(desk) =>
          // Create the booking and store it in the database
          val booking = Booking(java.util.UUID.randomUUID().toString, userId, Some(deskId), None, startTime, endTime)
          repository.bookDesk(booking).map(_ => booking.asRight[BookingError])
      }
    } yield result
  }
}
