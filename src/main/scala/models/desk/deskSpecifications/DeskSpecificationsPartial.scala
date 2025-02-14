package models.desk.deskSpecifications

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import models.desk.deskSpecifications.DeskType
import models.OpeningHours

import java.time.LocalDateTime

case class DeskSpecificationsPartial(
  deskId: String,
  deskName: String,
  description: Option[String],
  deskType: Option[DeskType],
  quantity: Option[Int],
  features: Option[List[String]],
  openingHours: Option[List[OpeningHours]],
  rules: Option[String]
)

object DeskSpecificationsPartial {
  implicit val encoder: Encoder[DeskSpecificationsPartial] = deriveEncoder[DeskSpecificationsPartial]
  implicit val decoder: Decoder[DeskSpecificationsPartial] = deriveDecoder[DeskSpecificationsPartial]
}
