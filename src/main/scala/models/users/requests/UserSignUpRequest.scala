package models.users.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.Role

import java.time.LocalDateTime

case class UserSignUpRequest(
                              user_id: String,
                              username: String,
                              password: String,
                              email: String,
                              role: Role,
                              created_at: LocalDateTime
                           )

object UserSignUpRequest {
  implicit val userSignupRequestEncoder: Encoder[UserSignUpRequest] = deriveEncoder[UserSignUpRequest]
  implicit val userSignupRequestDecoder: Decoder[UserSignUpRequest] = deriveDecoder[UserSignUpRequest]
}

