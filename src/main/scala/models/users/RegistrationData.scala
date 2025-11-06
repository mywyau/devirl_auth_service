package models.users

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.UserType

case class RegistrationData(
  username: String,
  email: String,
  firstName: String,
  lastName: String,
  userType: UserType
)

object RegistrationData {
  implicit val encoder: Encoder[RegistrationData] = deriveEncoder[RegistrationData]
  implicit val decoder: Decoder[RegistrationData] = deriveDecoder[RegistrationData]
}
