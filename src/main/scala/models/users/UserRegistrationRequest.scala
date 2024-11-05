package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UserRegistrationRequest(
                                    userId: String,
                                    username: String,
                                    password: String,
                                    first_name: String,
                                    last_name: String,
                                    street: String,
                                    city: String,
                                    country: String,
                                    county: Option[String],
                                    postcode: String,
                                    contact_number: String,
                                    email: String,
                                    role: Role,
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