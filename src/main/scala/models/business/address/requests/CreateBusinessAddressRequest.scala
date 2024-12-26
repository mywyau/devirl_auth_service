package models.business.address.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class CreateBusinessAddressRequest(
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
                                         longitude: Option[BigDecimal]
                                       )

object CreateBusinessAddressRequest {
  implicit val createBusinessAddressRequestEncoder: Encoder[CreateBusinessAddressRequest] = deriveEncoder[CreateBusinessAddressRequest]
  implicit val createBusinessAddressRequestDecoder: Decoder[CreateBusinessAddressRequest] = deriveDecoder[CreateBusinessAddressRequest]
}

