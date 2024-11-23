package models.wanderer.wanderer_personal_details.responses.success

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedPersonalDetailsResponse(response: String)

object CreatedPersonalDetailsResponse {

  implicit val personalDetailsEncoder: Encoder[CreatedPersonalDetailsResponse] = deriveEncoder[CreatedPersonalDetailsResponse]
  implicit val personalDetailsDecoder: Decoder[CreatedPersonalDetailsResponse] = deriveDecoder[CreatedPersonalDetailsResponse]
}