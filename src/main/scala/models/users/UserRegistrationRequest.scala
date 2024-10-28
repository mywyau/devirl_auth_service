package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UserRegistrationRequest(
                                    username: String,
                                    password: String,
                                    first_name: String,
                                    last_name: String,
                                    contact_number: String,
                                    role: Role,
                                    email: String
                                  )


object UserRegistrationRequest {
  implicit val encoder: Encoder[UserRegistrationRequest] = deriveEncoder[UserRegistrationRequest]
  implicit val decoder: Decoder[UserRegistrationRequest] = deriveDecoder[UserRegistrationRequest]
}

case class UserLoginRequest(
                             username: String,
                             password: String
                           )

object UserLoginRequest {
  implicit val encoder: Encoder[UserLoginRequest] = deriveEncoder[UserLoginRequest]
  implicit val decoder: Decoder[UserLoginRequest] = deriveDecoder[UserLoginRequest]
}