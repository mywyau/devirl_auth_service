package models.business.address.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class BusinessAddressRequest(
                                   userId: String,
                                   businessId: Option[String],
                                   businessName: Option[String],
                                   buildingName: Option[String],
                                   floorNumber: Option[String],
                                   street: Option[String],
                                   city: Option[String],
                                   country: Option[String],
                                   county: Option[String],
                                   postcode: Option[String],
                                   latitude: Option[BigDecimal],
                                   longitude: Option[BigDecimal],
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessAddressRequest {
  implicit val businessAddressRequestEncoder: Encoder[BusinessAddressRequest] = deriveEncoder[BusinessAddressRequest]
  implicit val businessAddressRequestDecoder: Decoder[BusinessAddressRequest] = deriveDecoder[BusinessAddressRequest]
}

