package models.office.specifications

import doobie.util.meta.Meta
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import models.OpeningHours

import java.time.LocalTime

case class OfficeAvailability(
  availability: List[OpeningHours]
)

object OfficeAvailability {
  implicit val encoder: Encoder[OfficeAvailability] = deriveEncoder[OfficeAvailability]
  implicit val decoder: Decoder[OfficeAvailability] = deriveDecoder[OfficeAvailability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[OfficeAvailability] =
    Meta[String].imap(jsonStr => decode[OfficeAvailability](jsonStr).getOrElse(throw new Exception("Invalid JSON")))(availability => availability.asJson.noSpaces)
}
