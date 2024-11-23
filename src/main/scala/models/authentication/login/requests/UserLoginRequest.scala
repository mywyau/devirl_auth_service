package models.authentication.login.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UserLoginRequest(
                             username: String,
                             password: String
                           )

object UserLoginRequest {
  implicit val userLoginRequestEncoder: Encoder[UserLoginRequest] = deriveEncoder[UserLoginRequest]
  implicit val userLoginRequestDecoder: Decoder[UserLoginRequest] = deriveDecoder[UserLoginRequest]
}

