package models.bookings.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedBookingResponse(response: String)

object CreatedBookingResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[CreatedBookingResponse] = deriveEncoder[CreatedBookingResponse]
  implicit val bookingDecoder: Decoder[CreatedBookingResponse] = deriveDecoder[CreatedBookingResponse]
}