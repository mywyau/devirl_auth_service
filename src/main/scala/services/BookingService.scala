package services

import cats._
import cats.implicits._
import models._
import repositories._

import java.time.LocalDateTime

trait BookingService[F[_]] {

  def getAllDesks: F[List[Desk]]

  def getAvailableDesks: F[List[Desk]]

  def getDeskById(deskId: String): F[Option[Desk]]

  def bookDesk(userId: String, deskId: String, roomId: String, startTime: LocalDateTime, endTime: LocalDateTime): F[Either[BookingError, Booking]]
}

sealed trait BookingError extends Throwable

case class DeskNotAvailable(deskId: String) extends BookingError

case class DeskNotFound(deskId: String) extends BookingError

class BookingServiceImpl[F[_] : Monad](repository: BookingRepository[F]) extends BookingService[F] {

  override def getAllDesks: F[List[Desk]] = {
    repository.getAllDesks
  }

  override def getAvailableDesks: F[List[Desk]] = {
    repository.getAvailableDesks
  }

  override def getDeskById(deskId: String): F[Option[Desk]] = {
    repository.findDeskById(deskId)
  }

  override def bookDesk(userId: String, deskId: String, roomId: String, startTime: LocalDateTime, endTime: LocalDateTime): F[Either[BookingError, Booking]] = {
    for {
      // Check if the desk exists in the repository
      maybeDesk <- repository.findDeskById(deskId)

      result <- maybeDesk match {
        case None =>
          // If the desk is not found, return an error
          DeskNotFound(deskId).asLeft[Booking].pure[F]

        case Some(desk) if desk.status != "available" =>
          // If the desk is not available, return an error
          DeskNotAvailable(deskId).asLeft[Booking].pure[F]

        case Some(desk) =>
          // Create the booking and store it in the repository
          val booking = Booking(
            id = java.util.UUID.randomUUID().toString,
            userId = userId,
            deskId = Some(deskId),
            roomId = Some(roomId),
            startTime = startTime,
            endTime = endTime
          )
          repository.bookDesk(booking).map(_ => booking.asRight[BookingError])
      }
    } yield result
  }

}
