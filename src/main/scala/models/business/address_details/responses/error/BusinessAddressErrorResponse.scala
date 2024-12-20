package models.business.address_details.responses.error

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class BusinessAddressErrorResponse(code:String, message:String)

object BusinessAddressErrorResponse {
  // Manually derive Encoder and Decoder for User
  implicit val businessAddressErrorEncoder: Encoder[BusinessAddressErrorResponse] = deriveEncoder[BusinessAddressErrorResponse]
  implicit val businessAddressErrorDecoder: Decoder[BusinessAddressErrorResponse] = deriveDecoder[BusinessAddressErrorResponse]
}