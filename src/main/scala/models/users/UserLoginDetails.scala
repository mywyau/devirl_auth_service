package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserLoginDetails(
                             userId: String,
                             username: String,
                             password_hash: String
                           )

object UserLoginDetails {
  implicit val userLoginEncoder: Encoder[UserLoginDetails] = deriveEncoder[UserLoginDetails]
  implicit val userLoginDecoder: Decoder[UserLoginDetails] = deriveDecoder[UserLoginDetails]
}

