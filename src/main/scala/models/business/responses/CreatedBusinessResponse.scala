package models.business.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedBusinessResponse(response: String)

object CreatedBusinessResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val businessEncoder: Encoder[CreatedBusinessResponse] = deriveEncoder[CreatedBusinessResponse]
  implicit val businessDecoder: Decoder[CreatedBusinessResponse] = deriveDecoder[CreatedBusinessResponse]
}