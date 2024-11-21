package models.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedResponse(message: String)

object CreatedResponse {
  implicit val createdEncoder: Encoder[CreatedResponse] = deriveEncoder[CreatedResponse]
  implicit val createdDecoder: Decoder[CreatedResponse] = deriveDecoder[CreatedResponse]
}