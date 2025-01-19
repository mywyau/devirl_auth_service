package models.deskSpecifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.desk.deskSpecifications.DeskType
import models.desk.deskSpecifications.Availability

case class DeskSpecifications(
  id: Option[Int],
  businessId: String,
  workspaceId: String,
  title: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  features: List[String],
  availability: Availability,
  rules: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object DeskSpecifications {
  implicit val encoder: Encoder[DeskSpecifications] = deriveEncoder[DeskSpecifications]
  implicit val decoder: Decoder[DeskSpecifications] = deriveDecoder[DeskSpecifications]
}
