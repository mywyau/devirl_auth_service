package models.desk.deskSpecifications.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskType

case class UpdateDeskSpecificationsRequest(
  deskName: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  features: List[String],
  availability: Availability,
  rules: Option[String]
)

object UpdateDeskSpecificationsRequest {
  implicit val encoder: Encoder[UpdateDeskSpecificationsRequest] = deriveEncoder[UpdateDeskSpecificationsRequest]
  implicit val decoder: Decoder[UpdateDeskSpecificationsRequest] = deriveDecoder[UpdateDeskSpecificationsRequest]
}
