package models.users.contact_details.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ContactDetailsErrorResponse(code:String, message:String)

object ContactDetailsErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val contactDetailsErrorEncoder: Encoder[ContactDetailsErrorResponse] = deriveEncoder[ContactDetailsErrorResponse]
  implicit val contactDetailsErrorDecoder: Decoder[ContactDetailsErrorResponse] = deriveDecoder[ContactDetailsErrorResponse]
}