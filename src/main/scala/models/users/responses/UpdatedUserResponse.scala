package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedUserResponse(response: String)


object UpdatedUserResponse {
  // Manually derive Encoder and Decoder for User
  implicit val userEncoder: Encoder[UpdatedUserResponse] = deriveEncoder[UpdatedUserResponse]
  implicit val userDecoder: Decoder[UpdatedUserResponse] = deriveDecoder[UpdatedUserResponse]
}

