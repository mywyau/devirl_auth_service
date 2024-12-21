package models.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeletedResponse(message: String)

object DeletedResponse {
  implicit val deletedEncoder: Encoder[DeletedResponse] = deriveEncoder[DeletedResponse]
  implicit val deletedDecoder: Decoder[DeletedResponse] = deriveDecoder[DeletedResponse]
}