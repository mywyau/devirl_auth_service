package models.office.contact_details.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class OfficeContactDetailsRequestErrorResponse(code:String, message:String)

object OfficeContactDetailsRequestErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val officeConactDetailsErrorEncoder: Encoder[OfficeContactDetailsRequestErrorResponse] = deriveEncoder[OfficeContactDetailsRequestErrorResponse]
  implicit val officeConactDetailsErrorDecoder: Decoder[OfficeContactDetailsRequestErrorResponse] = deriveDecoder[OfficeContactDetailsRequestErrorResponse]
}