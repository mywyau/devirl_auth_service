package controllers

import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.HttpRoutes
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.dsl.io._
import services.BookingService

import java.time.LocalDateTime

// Define BookingRequest
case class BookingRequest(
                           userId: String,
                           deskId: String,
                           roomId: String,
                           startTime: LocalDateTime,
                           endTime: LocalDateTime
                         )

object DeskController {
  def deskRoutes(bookingService: BookingService[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "desks" / "available" =>
        for {
          desks <- bookingService.getAvailableDesks
          resp <- Ok(desks.toString) // Implement proper response
        } yield resp

      case GET -> Root / "desk" / id =>
        for {
          deskOpt <- bookingService.getDeskById(id) // Fetch desk by ID
          resp <- deskOpt match {
            case Some(desk) => Ok(desk) // Automatically serialized to JSON
            case None => NotFound(s"No desk found with id: $id")
          }
        } yield resp

      case GET -> Root / "desks" =>
        for {
          desks <- bookingService.getAvailableDesks
          resp <- Ok(desks.toString) // Implement proper response
        } yield resp

      case req@POST -> Root / "desk" / "book" =>
        req.attemptAs[BookingRequest].value.flatMap {
          case Left(err) =>
            // Handle deserialization errors
            BadRequest(s"Invalid request body: ${err.getMessage}")

          case Right(bookingData) =>
            // Log the booking data (if needed)
            println(bookingData)

            // Call the booking service
            bookingService.bookDesk(
              userId = bookingData.userId,
              deskId = bookingData.deskId,
              roomId = bookingData.roomId,
              startTime = bookingData.startTime,
              endTime = bookingData.endTime
            ).flatMap {
              case Right(booking) =>
                // Return the booking in a proper JSON response
                Ok(booking.asJson)

              case Left(error) =>
                // Return a Conflict response with error message
                Conflict(Map("error" -> error.getMessage).asJson)
            }.handleErrorWith { err =>
              // Catch unexpected errors (such as database issues) and return a 500
              InternalServerError(s"Something went wrong: ${err.getMessage}")
            }
        }
    }
}
