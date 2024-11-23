package models.wanderer.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserAddress(
                        userId: String,
                        street: Option[String],
                        city: Option[String],
                        country: Option[String],
                        county: Option[String],
                        postcode: Option[String],
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime
                      )

object UserAddress {
  implicit val userAddressEncoder: Encoder[UserAddress] = deriveEncoder[UserAddress]
  implicit val userAddressDecoder: Decoder[UserAddress] = deriveDecoder[UserAddress]
}

