package models.business.specifications.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class BusinessSpecsErrorResponse(code: String, message: String)

object BusinessSpecsErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val businessSpecsErrorEncoder: Encoder[BusinessSpecsErrorResponse] = deriveEncoder[BusinessSpecsErrorResponse]
  implicit val businessSpecsErrorDecoder: Decoder[BusinessSpecsErrorResponse] = deriveDecoder[BusinessSpecsErrorResponse]
}