package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserContactDetails(
                 userId: String,
                 street: String,
                 city: String,
                 country: String,
                 county: Option[String],
                 postcode: String,
                 created_at: LocalDateTime,
                 updated_at: LocalDateTime
               )

object UserContactDetails {
  implicit val userAddressEncoder: Encoder[UserContactDetails] = deriveEncoder[UserContactDetails]
  implicit val userAddressDecoder: Decoder[UserContactDetails] = deriveDecoder[UserContactDetails]
}

