package models.desk.deskSpecifications

import doobie.util.meta.Meta
import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*

import java.time.LocalDateTime
import java.time.LocalTime

case class Availability(
  days: List[String], 
  startTime: LocalTime,
  endTime: LocalTime
)

object Availability {

  implicit val encoder: Encoder[Availability] = deriveEncoder[Availability]
  implicit val decoder: Decoder[Availability] = deriveDecoder[Availability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[Availability] =
    Meta[String].imap(jsonStr => 
      decode[Availability](jsonStr)
    .getOrElse(throw new Exception("Invalid JSON"))
    )(availability => availability.asJson.noSpaces)
}
