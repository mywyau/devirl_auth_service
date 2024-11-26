package models.office.office_specs

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class OfficeAvailability(
                               days: List[String],
                               startTime: LocalDateTime,
                               endTime: LocalDateTime
                             )


object OfficeAvailability {
  implicit val officeAvailabilityEncoder: Encoder[OfficeAvailability] = deriveEncoder[OfficeAvailability]
  implicit val officeAvailabilityDecoder: Decoder[OfficeAvailability] = deriveDecoder[OfficeAvailability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[OfficeAvailability] =
    Meta[String].imap(
      jsonStr => decode[OfficeAvailability](jsonStr).getOrElse(throw new Exception("Invalid JSON"))
    )(
      availability => availability.asJson.noSpaces
    )
}