package models.bookings.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedBookingResponse(response: String)


object UpdatedBookingResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[UpdatedBookingResponse] = deriveEncoder[UpdatedBookingResponse]
  implicit val bookingDecoder: Decoder[UpdatedBookingResponse] = deriveDecoder[UpdatedBookingResponse]
}

