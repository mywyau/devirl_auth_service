package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteBusinessResponse(response: String)

object DeleteBusinessResponse {
  // Manually derive Encoder and Decoder for Business
  implicit val BusinessEncoder: Encoder[DeleteBusinessResponse] = deriveEncoder[DeleteBusinessResponse]
  implicit val BusinessDecoder: Decoder[DeleteBusinessResponse] = deriveDecoder[DeleteBusinessResponse]
}