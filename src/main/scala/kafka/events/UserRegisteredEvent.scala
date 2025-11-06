package kafka.events

import io.circe.generic.semiauto.*
import io.circe.{Decoder, Encoder}
import java.time.Instant
import models.UserType

final case class UserRegisteredEvent(
  userId: String,
  username: String,
  email: String,
  userType: UserType,
  createdAt: Instant,
  version: Int = 1
)

object UserRegisteredEvent {
  given Encoder[UserRegisteredEvent] = deriveEncoder
  given Decoder[UserRegisteredEvent] = deriveDecoder
}
