package models.office.address_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class UpdateOfficeAddressRequest(
                                       buildingName: Option[String],
                                       floorNumber: Option[String],
                                       street: Option[String],
                                       city: Option[String],
                                       country: Option[String],
                                       county: Option[String],
                                       postcode: Option[String],
                                       latitude: Option[BigDecimal],
                                       longitude: Option[BigDecimal],
                                       updatedAt: LocalDateTime
                                     )

object UpdateOfficeAddressRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateOfficeAddressRequest] = deriveEncoder[UpdateOfficeAddressRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateOfficeAddressRequest] = deriveDecoder[UpdateOfficeAddressRequest]
}
