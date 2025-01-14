package models.responses

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class DeletedResponse(code: String, message: String)

object DeletedResponse {
  implicit val deletedEncoder: Encoder[DeletedResponse] = deriveEncoder[DeletedResponse]
  implicit val deletedDecoder: Decoder[DeletedResponse] = deriveDecoder[DeletedResponse]
}
