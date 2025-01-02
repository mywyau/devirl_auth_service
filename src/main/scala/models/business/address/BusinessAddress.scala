package models.business.address

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class BusinessAddress(
                            id: Option[Int],
                            userId: String,
                            businessId: String,
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

object BusinessAddress {
  implicit val businessAddressEncoder: Encoder[BusinessAddress] = deriveEncoder[BusinessAddress]
  implicit val businessAddressDecoder: Decoder[BusinessAddress] = deriveDecoder[BusinessAddress]
}
