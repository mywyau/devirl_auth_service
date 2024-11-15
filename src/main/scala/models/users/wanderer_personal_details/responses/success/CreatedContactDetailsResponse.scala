package models.users.wanderer_personal_details.responses.success

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedContactDetailsResponse(response: String)

object CreatedContactDetailsResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val contactDetailsEncoder: Encoder[CreatedContactDetailsResponse] = deriveEncoder[CreatedContactDetailsResponse]
  implicit val contactDetailsDecoder: Decoder[CreatedContactDetailsResponse] = deriveDecoder[CreatedContactDetailsResponse]
}