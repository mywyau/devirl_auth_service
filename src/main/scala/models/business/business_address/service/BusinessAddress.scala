package models.business.business_address.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class BusinessAddress(
                            id: Option[Int],
                            userId: String,
                            street: Option[String],
                            city: Option[String],
                            country: Option[String],
                            county: Option[String],
                            postcode: Option[String],
                            createdAt: LocalDateTime,
                            updatedAt: LocalDateTime
                          )

object BusinessAddress {
  implicit val businessAddressEncoder: Encoder[BusinessAddress] = deriveEncoder[BusinessAddress]
  implicit val businessAddressDecoder: Decoder[BusinessAddress] = deriveDecoder[BusinessAddress]
}
