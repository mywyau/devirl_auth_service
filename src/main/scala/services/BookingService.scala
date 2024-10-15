package services

import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits._
import models.Booking
import repositories.BookingRepository

import java.time.LocalDateTime


trait BookingService[F[_]] {

  def findBookingById(bookingId: String): F[Either[ValidationError, Booking]]

  def createBooking(booking: Booking): F[Either[ValidationError, Int]]

  def updateBooking(bookingId: String, updatedBooking: Booking): F[Either[ValidationError, Int]]

  def deleteBooking(bookingId: String): F[Either[ValidationError, Int]]
}

sealed trait ValidationError

case object InvalidBookingId extends ValidationError

case object InvalidTimeRange extends ValidationError

case object BookingNotFound extends ValidationError

case object OverlappingBooking extends ValidationError

class BookingServiceImpl[F[_] : Concurrent](repository: BookingRepository[F]) extends BookingService[F] {

  // Validation function for booking ID (example: make sure it's not empty or too short)
  def validateBookingId(bookingId: String): ValidatedNel[ValidationError, String] = {
    if (bookingId.nonEmpty && bookingId.length >= 3) bookingId.validNel
    else InvalidBookingId.invalidNel
  }

  // Validation function for time range (make sure start_time is before end_time)
  def validateTimeRange(startTime: LocalDateTime, endTime: LocalDateTime): ValidatedNel[ValidationError, (LocalDateTime, LocalDateTime)] = {
    if (startTime.isBefore(endTime))
      (startTime, endTime).validNel
    else
      InvalidTimeRange.invalidNel
  }

  // Validate if a booking exists by its ID
  def validateBookingExists(bookingId: String): EitherT[F, ValidationError, Booking] = {
    EitherT.fromOptionF(repository.findBookingById(bookingId), BookingNotFound)
  }

  // Find booking by ID with validation
  def findBookingById(bookingId: String): F[Either[ValidationError, Booking]] = {
    val validation = validateBookingId(bookingId)

    validation match {
      case Valid(_) => repository.findBookingById(bookingId).map {
        case Some(booking) =>
          Right(booking)
        case None =>
          Left(BookingNotFound)
      }
      case Invalid(errors) =>
        Concurrent[F].pure(Left(errors.head)) // Return the first validation error
    }
  }

  // Create booking with validations (including time range and overlap check)
  def createBooking(booking: Booking): F[Either[ValidationError, Int]] = {
    val validation = validateTimeRange(booking.start_time, booking.end_time)

    validation match {
      case Valid(_) =>
        for {
          isOverlapping <- repository.doesOverlap(booking)
          result <- if (isOverlapping) {
            Concurrent[F].pure(Left(OverlappingBooking))
          } else {
            repository.setBooking(booking).map(Right(_))
          }
        } yield result
      case Invalid(errors) =>
        Concurrent[F].pure(Left(errors.head)) // For simplicity, return the first error
    }
  }

  // Update booking with validation (ensures booking exists before updating)
  def updateBooking(bookingId: String, updatedBooking: Booking): F[Either[ValidationError, Int]] = {
    val validation = validateTimeRange(updatedBooking.start_time, updatedBooking.end_time)

    validation match {
      case Valid(_) =>
        validateBookingExists(bookingId).value.flatMap {
          case Right(_) =>
            for {
              isOverlapping <- repository.doesOverlap(updatedBooking)
              result <- if (isOverlapping) {
                Concurrent[F].pure(Left(OverlappingBooking))
              } else {
                repository.updateBooking(bookingId, updatedBooking).map(Right(_))
              }
            } yield result
          case Left(error) => Concurrent[F].pure(Left(error))
        }
      case Invalid(errors) =>
        Concurrent[F].pure(Left(errors.head)) // Return the first error for simplicity
    }
  }

  // Delete booking with validation (ensures booking exists before deleting)
  def deleteBooking(bookingId: String): F[Either[ValidationError, Int]] = {
    validateBookingExists(bookingId).value.flatMap {
      case Right(_) => repository.deleteBooking(bookingId).map(Right(_))
      case Left(error) => Concurrent[F].pure(Left(error))
    }
  }
}
