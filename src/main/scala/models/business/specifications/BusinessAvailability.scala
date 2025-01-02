package models.business.specifications

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

import java.time.LocalTime

case class BusinessAvailability(
                                 days: List[String],
                                 startTime: LocalTime,
                                 endTime: LocalTime
                               )


object BusinessAvailability {
  implicit val businessAvailabilityEncoder: Encoder[BusinessAvailability] = deriveEncoder[BusinessAvailability]
  implicit val businessAvailabilityDecoder: Decoder[BusinessAvailability] = deriveDecoder[BusinessAvailability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[BusinessAvailability] =
    Meta[String].imap(
      jsonStr => decode[BusinessAvailability](jsonStr).getOrElse(throw new Exception("Invalid JSON"))
    )(
      availability => availability.asJson.noSpaces
    )
}