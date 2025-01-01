package models.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedResponse(message: String)

object UpdatedResponse {
  implicit val updatedResponseEncoder: Encoder[UpdatedResponse] = deriveEncoder[UpdatedResponse]
  implicit val updatedResponseDecoder: Decoder[UpdatedResponse] = deriveDecoder[UpdatedResponse]
}