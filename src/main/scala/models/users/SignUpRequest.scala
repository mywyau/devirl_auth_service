package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class SignUpRequest(
                          userId: String,
                          username: String,
                          password: String,
                          role: Role,
                          email: String
                        )


object SignUpRequest {
  implicit val encoder: Encoder[SignUpRequest] = deriveEncoder[SignUpRequest]
  implicit val decoder: Decoder[SignUpRequest] = deriveDecoder[SignUpRequest]
}