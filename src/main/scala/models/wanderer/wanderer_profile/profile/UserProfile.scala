package models.wanderer.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserProfile(
                        userId: String,
                        userLoginDetails: UserLoginDetails,
                        firstName: String,
                        lastName: String,
                        userAddress: UserAddress,
                        contactNumber: String,
                        email: String,
                        role: Role,
                        createdAt: LocalDateTime,
                        updatedAt: LocalDateTime
                      )

object UserProfile {
  implicit val userProfileEncoder: Encoder[UserProfile] = deriveEncoder[UserProfile]
  implicit val userProfileDecoder: Decoder[UserProfile] = deriveDecoder[UserProfile]
}