package models.business.specifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.OpeningHours

case class BusinessSpecifications(
  id: Option[Int],
  userId: String,
  businessId: String,
  businessName: Option[String],
  description: Option[String],
  openingHours: Option[List[OpeningHours]],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object BusinessSpecifications {

  implicit val businessSpecificationsEncoder: Encoder[BusinessSpecifications] = deriveEncoder[BusinessSpecifications]
  implicit val businessSpecificationsDecoder: Decoder[BusinessSpecifications] = deriveDecoder[BusinessSpecifications]
}
