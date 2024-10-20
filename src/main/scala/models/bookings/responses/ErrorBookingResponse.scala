package models.bookings.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorBookingResponse(response: String)

object ErrorBookingResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[ErrorBookingResponse] = deriveEncoder[ErrorBookingResponse]
  implicit val bookingDecoder: Decoder[ErrorBookingResponse] = deriveDecoder[ErrorBookingResponse]
}