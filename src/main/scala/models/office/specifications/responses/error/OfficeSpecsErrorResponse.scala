package models.office.specifications.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class OfficeSpecsErrorResponse(code:String, message:String)

object OfficeSpecsErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val officeSpecsErrorEncoder: Encoder[OfficeSpecsErrorResponse] = deriveEncoder[OfficeSpecsErrorResponse]
  implicit val officeSpecsErrorDecoder: Decoder[OfficeSpecsErrorResponse] = deriveDecoder[OfficeSpecsErrorResponse]
}