package models.wanderer.wanderer_profile.profile

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserLoginDetails(
                             id: Option[Int],
                             userId: String,
                             username: String,
                             passwordHash: String,
                             email: String,
                             role: Role,
                             createdAt: LocalDateTime,
                             updatedAt: LocalDateTime
                           )

object UserLoginDetails {
  implicit val userLoginDetailsEncoder: Encoder[UserLoginDetails] = deriveEncoder[UserLoginDetails]
  implicit val userLoginDetailsDecoder: Decoder[UserLoginDetails] = deriveDecoder[UserLoginDetails]
}

