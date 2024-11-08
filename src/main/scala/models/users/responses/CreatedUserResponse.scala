package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedUserResponse(response: String)

object CreatedUserResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val userEncoder: Encoder[CreatedUserResponse] = deriveEncoder[CreatedUserResponse]
  implicit val userDecoder: Decoder[CreatedUserResponse] = deriveDecoder[CreatedUserResponse]
}