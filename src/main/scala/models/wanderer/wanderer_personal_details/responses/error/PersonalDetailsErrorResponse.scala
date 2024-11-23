package models.wanderer.wanderer_personal_details.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class PersonalDetailsErrorResponse(code:String, message:String)

object PersonalDetailsErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val personalDetailsErrorEncoder: Encoder[PersonalDetailsErrorResponse] = deriveEncoder[PersonalDetailsErrorResponse]
  implicit val personalDetailsErrorDecoder: Decoder[PersonalDetailsErrorResponse] = deriveDecoder[PersonalDetailsErrorResponse]
}