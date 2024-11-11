package models.users.login.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserLoginRequest(
                             username: String,
                             password: String
                           )

object UserLoginRequest {
  implicit val userLoginRequestEncoder: Encoder[UserLoginRequest] = deriveEncoder[UserLoginRequest]
  implicit val userLoginRequestDecoder: Decoder[UserLoginRequest] = deriveDecoder[UserLoginRequest]
}

