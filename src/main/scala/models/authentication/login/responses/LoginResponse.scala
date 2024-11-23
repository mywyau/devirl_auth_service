package models.authentication.login.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

case class LoginResponse(
                          userId: String,
                          username: String,
                          passwordHash: String,
                          email: String,
                          role: Role,
                        )

object LoginResponse {
  implicit val userEncoder: Encoder[LoginResponse] = deriveEncoder[LoginResponse]
  implicit val userDecoder: Decoder[LoginResponse] = deriveDecoder[LoginResponse]
}