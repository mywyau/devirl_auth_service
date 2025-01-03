package models.business.desk_listing

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class Availability(days: List[String], startTime: LocalDateTime, endTime: LocalDateTime)

object Availability {
  implicit val availabilityEncoder: Encoder[Availability] = deriveEncoder[Availability]
  implicit val availabilityDecoder: Decoder[Availability] = deriveDecoder[Availability]

  // Define Doobie Meta for JSON
  implicit val availabilityMeta: Meta[Availability] =
    Meta[String].imap(jsonStr => decode[Availability](jsonStr).getOrElse(throw new Exception("Invalid JSON")))(availability => availability.asJson.noSpaces)
}
