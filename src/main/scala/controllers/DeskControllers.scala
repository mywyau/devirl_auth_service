package controllers

import cats.effect.IO
import io.circe.generic.auto._       // Automatic derivation of encoders/decoders
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import org.http4s.circe.CirceEntityDecoder._  // Enables JSON decoding using Circe
import services.BookingService
import java.time.LocalDateTime

// Define BookingRequest
case class BookingRequest(
                           userId: String,
                           deskId: String,
                           startTime: LocalDateTime,
                           endTime: LocalDateTime
                         )

object DeskController {
  def deskRoutes(bookingService: BookingService[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "desks" =>
      for {
        desks <- bookingService.getAvailableDesks
        resp <- Ok(desks.toString) // Implement proper response
      } yield resp

    // POST route to book a desk
    case req @ POST -> Root / "desk" / "book" =>
      for {
        // Deserialize JSON body into BookingRequest
        bookingData <- req.as[BookingRequest]
        result <- bookingService.bookDesk(
          bookingData.userId,
          bookingData.deskId,
          bookingData.startTime,
          bookingData.endTime
        )
        response <- result match {
          case Right(booking) => Ok(booking.toString)  // Replace with proper response
          case Left(error) => Conflict(error.getMessage)
        }
      } yield response
  }
}
