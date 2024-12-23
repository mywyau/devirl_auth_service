package models.office.address_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class OfficeAddressRequest(
                                 businessId: String,
                                 officeId: String,
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

object OfficeAddressRequest {
  implicit val officeAddressRequestEncoder: Encoder[OfficeAddressRequest] = deriveEncoder[OfficeAddressRequest]
  implicit val officeAddressRequestDecoder: Decoder[OfficeAddressRequest] = deriveDecoder[OfficeAddressRequest]
}
