package models.business.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteBusinessResponse(response: String)

object DeleteBusinessResponse {
  // Manually derive Encoder and Decoder for Business
  implicit val businessEncoder: Encoder[DeleteBusinessResponse] = deriveEncoder[DeleteBusinessResponse]
  implicit val businessDecoder: Decoder[DeleteBusinessResponse] = deriveDecoder[DeleteBusinessResponse]
}