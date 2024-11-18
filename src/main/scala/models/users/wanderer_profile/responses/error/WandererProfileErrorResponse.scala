package models.users.wanderer_profile.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.responses.ErrorResponse

case class WandererProfileErrorResponse(loginDetailsErrors: List[ErrorResponse], addressErrors: List[ErrorResponse], contactDetailsErrors: List[ErrorResponse], otherErrors: List[ErrorResponse])

object WandererProfileErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val wandererProfileErrorEncoder: Encoder[WandererProfileErrorResponse] = deriveEncoder[WandererProfileErrorResponse]
  implicit val wandererProfileErrorDecoder: Decoder[WandererProfileErrorResponse] = deriveDecoder[WandererProfileErrorResponse]
}