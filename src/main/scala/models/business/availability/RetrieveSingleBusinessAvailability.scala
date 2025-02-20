package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalTime
import models.Day

case class RetrieveSingleBusinessAvailability(
  weekday: Day,
  openingTime: Option[LocalTime],
  closingTime: Option[LocalTime]
)

object RetrieveSingleBusinessAvailability {
  implicit val encoder: Encoder[RetrieveSingleBusinessAvailability] = deriveEncoder[RetrieveSingleBusinessAvailability]
  implicit val decoder: Decoder[RetrieveSingleBusinessAvailability] = deriveDecoder[RetrieveSingleBusinessAvailability]
}
