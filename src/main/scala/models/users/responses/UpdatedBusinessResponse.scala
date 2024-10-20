package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedBusinessResponse(response: String)


object UpdatedBusinessResponse {
  // Manually derive Encoder and Decoder for Business
  implicit val BusinessEncoder: Encoder[UpdatedBusinessResponse] = deriveEncoder[UpdatedBusinessResponse]
  implicit val BusinessDecoder: Decoder[UpdatedBusinessResponse] = deriveDecoder[UpdatedBusinessResponse]
}

