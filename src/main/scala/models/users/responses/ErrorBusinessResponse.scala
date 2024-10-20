package models.users.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorBusinessResponse(response: String)

object ErrorBusinessResponse {
  // Manually derive Encoder and Decoder for Business
  implicit val BusinessEncoder: Encoder[ErrorBusinessResponse] = deriveEncoder[ErrorBusinessResponse]
  implicit val BusinessDecoder: Decoder[ErrorBusinessResponse] = deriveDecoder[ErrorBusinessResponse]
}