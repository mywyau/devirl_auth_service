package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteUserResponse(response: String)

object DeleteUserResponse {
  // Manually derive Encoder and Decoder for User
  implicit val userEncoder: Encoder[DeleteUserResponse] = deriveEncoder[DeleteUserResponse]
  implicit val userDecoder: Decoder[DeleteUserResponse] = deriveDecoder[DeleteUserResponse]
}