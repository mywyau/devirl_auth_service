package controllers.bookings

import cats.effect.{Concurrent, IO}
import controllers.{BookingController, BookingControllerImpl}
import io.circe.syntax._
import models.Booking
import models.bookings._
import models.bookings.errors.ValidationError
import models.bookings.responses.{CreatedBookingResponse, DeleteBookingResponse, UpdatedBookingResponse}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Response, Status}
import services.BookingService
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}

object TestBookingController {
  def apply[F[_] : Concurrent](bookingService: BookingService[F]): BookingController[F] =
    new BookingControllerImpl[F](bookingService)
}

class MockBookingService extends BookingService[IO] {

  // Sample booking data
  val sampleBooking_1: Booking =
    Booking(
      id = Some(1),
      booking_id = "booking_1",
      booking_name = "Sample Booking 1",
      user_id = 1,
      workspace_id = 1,
      booking_date = LocalDate.of(2024, 10, 10),
      start_time = LocalDateTime.of(2024, 10, 10, 9, 0),
      end_time = LocalDateTime.of(2024, 10, 10, 12, 0),
      status = Confirmed,
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  override def findBookingById(bookingId: String): IO[Either[ValidationError, Booking]] =
    IO.pure(Right(sampleBooking_1))

  override def createBooking(booking: Booking): IO[Either[ValidationError, Int]] =
    IO.pure(Right(1))

  override def updateBooking(bookingId: String, booking: Booking): IO[Either[ValidationError, Int]] =
    IO.pure(Right(1))

  override def deleteBooking(bookingId: String): IO[Either[ValidationError, Int]] =
    IO.pure(Right(1))
}

object BookingControllerSpec extends SimpleIOSuite {

  val bookingService = new MockBookingService

  test("GET /bookings/:bookingId should return the booking when it exists") {

    val controller = BookingController[IO](bookingService)

    val request = Request[IO](Method.GET, uri"/bookings/1")
    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      booking <- response.as[Booking]
    } yield {
      expect.all(
        response.status == Status.Ok,
        booking == bookingService.sampleBooking_1
      )
    }
  }

  test("POST /bookings should create a new booking and return Created status with body") {
    val bookingService = new MockBookingService
    val controller = BookingController[IO](bookingService)

    // Sample booking to be sent in POST request
    val newBooking =
      Booking(
        id = None, // New bookings don't have an ID yet
        booking_id = "booking_new",
        booking_name = "New Booking",
        user_id = 2,
        workspace_id = 1,
        booking_date = LocalDate.of(2024, 12, 1),
        start_time = LocalDateTime.of(2024, 12, 1, 10, 0),
        end_time = LocalDateTime.of(2024, 12, 1, 12, 0),
        status = Confirmed,
        created_at = LocalDateTime.of(2024, 11, 25, 15, 0)
      )

    // Create a POST request with the booking as JSON
    val request = Request[IO](
      method = Method.POST,
      uri = uri"/bookings"
    ).withEntity(newBooking.asJson) // Encode booking as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[CreatedBookingResponse]
    } yield {
      expect.all(
        response.status == Status.Created,
        body.response.contains("Booking created successfully")
      )
    }
  }

  test("PUT /bookings/:bookingId should update a old booking and return OK status with body") {

    val bookingService = new MockBookingService
    val controller = BookingController[IO](bookingService)

    // Sample booking to be sent in POST request
    val updatedBooking = Booking(
      id = None, // New bookings don't have an ID yet
      booking_id = "booking_updated",
      booking_name = "Updated Booking",
      user_id = 1,
      workspace_id = 1,
      booking_date = LocalDate.of(2024, 12, 1),
      start_time = LocalDateTime.of(2024, 12, 1, 13, 0),
      end_time = LocalDateTime.of(2024, 12, 1, 15, 0),
      status = Confirmed,
      created_at = LocalDateTime.of(2024, 11, 25, 15, 0)
    )

    // Create a POST request with the booking as JSON
    val request = Request[IO](
      method = Method.PUT,
      uri = uri"/bookings/1"
    ).withEntity(updatedBooking.asJson) // Encode booking as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[UpdatedBookingResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Booking updated successfully")
      )
    }
  }

  test("DELETE /bookings/:bookingId should update a old booking and return OK status with body") {

    val bookingService = new MockBookingService
    val controller = BookingController[IO](bookingService)

    // Create a POST request with the booking as JSON
    val request = Request[IO](
      method = Method.DELETE,
      uri = uri"/bookings/1"
    ) // Encode booking as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[DeleteBookingResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Booking deleted successfully")
      )
    }
  }
}
