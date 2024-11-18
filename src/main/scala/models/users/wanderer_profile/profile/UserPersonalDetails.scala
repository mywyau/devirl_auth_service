package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserPersonalDetails(
                                userId: String,
                                firstName: Option[String],
                                lastName: Option[String],
                                contactNumber: Option[String],
                                email: Option[String],
                                company: Option[String],
                                createdAt: LocalDateTime,
                                updatedAt: LocalDateTime
                              )

object UserPersonalDetails {
  implicit val userAddressEncoder: Encoder[UserPersonalDetails] = deriveEncoder[UserPersonalDetails]
  implicit val userAddressDecoder: Decoder[UserPersonalDetails] = deriveDecoder[UserPersonalDetails]
}

