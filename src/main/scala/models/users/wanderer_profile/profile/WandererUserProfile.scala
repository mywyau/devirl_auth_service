package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class WandererUserProfile(
                                userId: String,
                                userLoginDetails: Option[UserLoginDetails],
                                first_name: Option[String],
                                last_name: Option[String],
                                userAddress: Option[UserAddress],
                                contact_number: Option[String],
                                email: Option[String],
                                role: Option[Role],
                                created_at: LocalDateTime,
                                updated_at: LocalDateTime
                              )

object WandererUserProfile {
  implicit val wandererUserProfileEncoder: Encoder[WandererUserProfile] = deriveEncoder[WandererUserProfile]
  implicit val wandererUserProfileDecoder: Decoder[WandererUserProfile] = deriveDecoder[WandererUserProfile]
}