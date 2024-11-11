package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserProfile(
                        userId: String,
                        userLoginDetails: UserLoginDetails,
                        first_name: String,
                        last_name: String,
                        userAddress: UserAddress,
                        contact_number: String,
                        email: String,
                        role: Role,
                        created_at: LocalDateTime,
                        updated_at: LocalDateTime
                      )

object UserProfile {
  implicit val userProfileEncoder: Encoder[UserProfile] = deriveEncoder[UserProfile]
  implicit val userProfileDecoder: Decoder[UserProfile] = deriveDecoder[UserProfile]
}