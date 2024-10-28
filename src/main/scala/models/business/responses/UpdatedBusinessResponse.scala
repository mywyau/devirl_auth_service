package models.business.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedBusinessResponse(response: String)


object UpdatedBusinessResponse {
  // Manually derive Encoder and Decoder for Business
  implicit val businessEncoder: Encoder[UpdatedBusinessResponse] = deriveEncoder[UpdatedBusinessResponse]
  implicit val businessDecoder: Decoder[UpdatedBusinessResponse] = deriveDecoder[UpdatedBusinessResponse]
}

