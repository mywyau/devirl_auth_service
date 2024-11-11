package models.users.wanderer_profile.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class RegistrationErrorResponse(usernameErrors: List[String], passwordErrors: List[String], emailErrors: List[String])

object RegistrationErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val registrationErrorEncoder: Encoder[RegistrationErrorResponse] = deriveEncoder[RegistrationErrorResponse]
  implicit val registrationErrorDecoder: Decoder[RegistrationErrorResponse] = deriveDecoder[RegistrationErrorResponse]
}