package models.users.registration.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class UserSignUpRequest(
                              userId: String,
                              username: String,
                              password: String,
                              email: String,
                              role: Role,
                              createdAt: LocalDateTime
                            )

object UserSignUpRequest {
  implicit val userSignupRequestEncoder: Encoder[UserSignUpRequest] = deriveEncoder[UserSignUpRequest]
  implicit val userSignupRequestDecoder: Decoder[UserSignUpRequest] = deriveDecoder[UserSignUpRequest]
}

