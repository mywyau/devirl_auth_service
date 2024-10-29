package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class LoginResponse(response: String)

object LoginResponse {
  implicit val userEncoder: Encoder[LoginResponse] = deriveEncoder[LoginResponse]
  implicit val userDecoder: Decoder[LoginResponse] = deriveDecoder[LoginResponse]
}