package models.users.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserLoginDetails(
                             id: Option[Int],
                             user_id: String,
                             username: String,
                             password_hash: String,
                             email: String,
                             role: Role,
                             created_at: LocalDateTime,
                             updated_at: LocalDateTime
                           )

object UserLoginDetails {
  implicit val userLoginDetailsEncoder: Encoder[UserLoginDetails] = deriveEncoder[UserLoginDetails]
  implicit val userLoginDetailsDecoder: Decoder[UserLoginDetails] = deriveDecoder[UserLoginDetails]
}

