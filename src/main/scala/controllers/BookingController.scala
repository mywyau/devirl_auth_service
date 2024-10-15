package controllers

import cats.effect.{Concurrent, Sync}
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.circe._
import io.circe.generic.auto._
import io.circe.syntax._
import models.Booking
import services._
import cats.implicits._

class BookingController[F[_]: Concurrent](bookingService: BookingService[F]) extends Http4sDsl[F] {

  // Create or get JSON decoder/encoder for Booking object (if needed)
  implicit val bookingDecoder: EntityDecoder[F, Booking] = jsonOf[F, Booking]

  // Define routes for the Booking Controller
  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Find booking by ID
    case GET -> Root / "bookings" / bookingId =>
      bookingService.findBookingById(bookingId).flatMap {
        case Right(booking) => Ok(booking.asJson)
        case Left(BookingNotFound) => NotFound("Booking not found")
        case Left(InvalidBookingId) => BadRequest("Invalid booking ID")
        case _ => InternalServerError("An error occurred")
      }

    // Create a new booking
    case req @ POST -> Root / "bookings" =>
      req.decode[Booking] { booking =>
        bookingService.createBooking(booking).flatMap {
          case Right(_) => Created("Booking created successfully")
          case Left(InvalidTimeRange) => BadRequest("Invalid time range")
          case Left(OverlappingBooking) => Conflict("Booking overlaps with another booking")
          case _ => InternalServerError("An error occurred")
        }
      }

    // Update an existing booking by ID
    case req @ PUT -> Root / "bookings" / bookingId =>
      req.decode[Booking] { updatedBooking =>
        bookingService.updateBooking(bookingId, updatedBooking).flatMap {
          case Right(_) => Ok("Booking updated successfully")
          case Left(BookingNotFound) => NotFound("Booking not found")
          case Left(InvalidTimeRange) => BadRequest("Invalid time range")
          case Left(OverlappingBooking) => Conflict("Booking overlaps with another booking")
          case _ => InternalServerError("An error occurred")
        }
      }

    // Delete a booking by ID
    case DELETE -> Root / "bookings" / bookingId =>
      bookingService.deleteBooking(bookingId).flatMap {
        case Right(_) => Ok("Booking deleted successfully")
        case Left(BookingNotFound) => NotFound("Booking not found")
        case _ => InternalServerError("An error occurred")
      }
  }
}
