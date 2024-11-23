package models.wanderer.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class WandererUserProfile(
                                userId: String,
                                userLoginDetails: Option[UserLoginDetails],
                                userPersonalDetails: Option[UserPersonalDetails],
                                userAddress: Option[UserAddress],
                                role: Option[Role],
                                createdAt: LocalDateTime,
                                updatedAt: LocalDateTime
                              )

object WandererUserProfile {
  implicit val wandererUserProfileEncoder: Encoder[WandererUserProfile] = deriveEncoder[WandererUserProfile]
  implicit val wandererUserProfileDecoder: Decoder[WandererUserProfile] = deriveDecoder[WandererUserProfile]
}