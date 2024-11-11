package models.users.login.errors

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class LoginErrorResponse(usernameErrors: List[String], passwordErrors: List[String])

object LoginErrorResponse {
  implicit val loginErrorEncoder: Encoder[LoginErrorResponse] = deriveEncoder[LoginErrorResponse]
  implicit val loginErrorDecoder: Decoder[LoginErrorResponse] = deriveDecoder[LoginErrorResponse]
}