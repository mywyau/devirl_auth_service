package models.users.login.errors

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.responses.ErrorResponse

case class LoginErrorResponse(usernameErrors: List[ErrorResponse], passwordErrors: List[ErrorResponse])

object LoginErrorResponse {
  implicit val loginErrorEncoder: Encoder[LoginErrorResponse] = deriveEncoder[LoginErrorResponse]
  implicit val loginErrorDecoder: Decoder[LoginErrorResponse] = deriveDecoder[LoginErrorResponse]
}