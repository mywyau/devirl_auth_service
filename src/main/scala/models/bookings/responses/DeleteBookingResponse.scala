package models.bookings.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteBookingResponse(response: String)

object DeleteBookingResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[DeleteBookingResponse] = deriveEncoder[DeleteBookingResponse]
  implicit val bookingDecoder: Decoder[DeleteBookingResponse] = deriveDecoder[DeleteBookingResponse]
}