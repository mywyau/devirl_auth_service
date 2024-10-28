package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorUserResponse(response: List[String])

object ErrorUserResponse {
  // Manually derive Encoder and Decoder for User
  implicit val userEncoder: Encoder[ErrorUserResponse] = deriveEncoder[ErrorUserResponse]
  implicit val userDecoder: Decoder[ErrorUserResponse] = deriveDecoder[ErrorUserResponse]
}