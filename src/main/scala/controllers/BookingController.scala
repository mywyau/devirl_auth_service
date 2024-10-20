package controllers

import cats.effect.Concurrent
import cats.implicits._
import io.circe.syntax._
import models.Booking
import models.bookings.errors._
import models.bookings.responses.{CreatedBookingResponse, DeleteBookingResponse, ErrorBookingResponse, UpdatedBookingResponse}
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import services._

trait BookingController[F[_]] {
  def routes: HttpRoutes[F]
}

object BookingController {
  def apply[F[_] : Concurrent](bookingService: BookingService[F]): BookingController[F] =
    new BookingControllerImpl[F](bookingService)
}

class BookingControllerImpl[F[_] : Concurrent](bookingService: BookingService[F]) extends BookingController[F] with Http4sDsl[F] {

  // Create or get JSON decoder/encoder for Booking object (if needed)
  implicit val bookingDecoder: EntityDecoder[F, Booking] = jsonOf[F, Booking]

  // Define routes for the Booking Controller
  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Find booking by ID
    case GET -> Root / "bookings" / bookingId =>
      bookingService.findBookingById(bookingId).flatMap {
        case Right(booking) => Ok(booking.asJson)
        case Left(BookingNotFound) => NotFound("Booking not found")
        case Left(InvalidBookingId) => BadRequest("Invalid booking ID")
        case _ => InternalServerError("An error occurred")
      }

    // Create a new booking
    case req@POST -> Root / "bookings" =>
      req.decode[Booking] { booking =>
        bookingService.createBooking(booking).flatMap {
          case Right(_) => Created(CreatedBookingResponse("Booking created successfully").asJson)
          case Left(InvalidTimeRange) => BadRequest(ErrorBookingResponse("Invalid time range").asJson)
          case Left(OverlappingBooking) => Conflict(ErrorBookingResponse("Booking overlaps with another booking").asJson)
          case _ => InternalServerError(ErrorBookingResponse("An error occurred").asJson)
        }
      }

    // Update an existing booking by ID
    case req@PUT -> Root / "bookings" / bookingId =>
      req.decode[Booking] { updatedBooking =>
        bookingService.updateBooking(bookingId, updatedBooking).flatMap {
          case Right(_) => Ok(UpdatedBookingResponse("Booking updated successfully").asJson)
          case Left(BookingNotFound) => NotFound(ErrorBookingResponse("Booking not found").asJson)
          case Left(InvalidTimeRange) => BadRequest(ErrorBookingResponse("Invalid time range").asJson)
          case Left(OverlappingBooking) => Conflict(ErrorBookingResponse("Booking overlaps with another booking").asJson)
          case _ => InternalServerError(ErrorBookingResponse("An error occurred").asJson)
        }
      }

    // Delete a booking by ID
    case DELETE -> Root / "bookings" / bookingId =>
      bookingService.deleteBooking(bookingId).flatMap {
        case Right(_) => Ok(DeleteBookingResponse("Booking deleted successfully").asJson)
        case Left(BookingNotFound) => NotFound(ErrorBookingResponse("Booking not found").asJson)
        case _ => InternalServerError(ErrorBookingResponse("An error occurred").asJson)
      }
  }
}
