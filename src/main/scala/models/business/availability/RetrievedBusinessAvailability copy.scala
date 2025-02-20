package models.business.availability

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalTime
import models.Day

case class RetrievedBusinessAvailability(
  availability: List[RetrieveSingleBusinessAvailability]
)

object RetrievedBusinessAvailability {
  implicit val encoder: Encoder[RetrievedBusinessAvailability] = deriveEncoder[RetrievedBusinessAvailability]
  implicit val decoder: Decoder[RetrievedBusinessAvailability] = deriveDecoder[RetrievedBusinessAvailability]
}
