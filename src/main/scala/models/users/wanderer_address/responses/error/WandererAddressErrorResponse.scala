package models.users.wanderer_address.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class WandererAddressErrorResponse(usernameErrors: List[String], passwordErrors: List[String], emailErrors: List[String])

object WandererAddressErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val wandererAddressErrorEncoder: Encoder[WandererAddressErrorResponse] = deriveEncoder[WandererAddressErrorResponse]
  implicit val wandererAddressErrorDecoder: Decoder[WandererAddressErrorResponse] = deriveDecoder[WandererAddressErrorResponse]
}