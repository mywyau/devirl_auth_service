package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserPersonalDetails(
                                user_id: String,
                                first_name: Option[String],
                                last_name: Option[String],
                                contact_number: Option[String],
                                email: Option[String],
                                company: Option[String],
                                created_at: LocalDateTime,
                                updated_at: LocalDateTime
                              )

object UserPersonalDetails {
  implicit val userAddressEncoder: Encoder[UserPersonalDetails] = deriveEncoder[UserPersonalDetails]
  implicit val userAddressDecoder: Decoder[UserPersonalDetails] = deriveDecoder[UserPersonalDetails]
}

