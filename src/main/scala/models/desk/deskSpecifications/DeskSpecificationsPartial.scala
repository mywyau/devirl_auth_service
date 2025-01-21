package models.desk.deskSpecifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskType

case class DeskSpecificationsPartial(
  deskId: String,
  deskName: String,
  description: Option[String],
  deskType: Option[DeskType],
  quantity: Option[Int],
  features: Option[List[String]],
  availability: Option[Availability],
  rules: Option[String]
)

object DeskSpecificationsPartial {
  implicit val encoder: Encoder[DeskSpecificationsPartial] = deriveEncoder[DeskSpecificationsPartial]
  implicit val decoder: Decoder[DeskSpecificationsPartial] = deriveDecoder[DeskSpecificationsPartial]
}
