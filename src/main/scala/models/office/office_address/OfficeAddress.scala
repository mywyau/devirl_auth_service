package models.office.office_address

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class OfficeAddress(
                          id: Option[Int],
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
                          longitude: Option[BigDecimal],
                          createdAt: LocalDateTime,
                          updatedAt: LocalDateTime
                        )

object OfficeAddress {
  implicit val officeAddressEncoder: Encoder[OfficeAddress] = deriveEncoder[OfficeAddress]
  implicit val officeAddressDecoder: Decoder[OfficeAddress] = deriveDecoder[OfficeAddress]
}
