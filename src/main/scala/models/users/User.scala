package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class User(
                 username: String,
                 password_hash: String,
                 first_name: String,
                 last_name: String,
                 contact_number: String,
                 email: String,
                 role: Role,
                 created_at: LocalDateTime
               )

object User {
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
}

