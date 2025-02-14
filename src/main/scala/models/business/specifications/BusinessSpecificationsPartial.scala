package models.business.specifications

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import models.OpeningHours

import java.time.LocalDateTime

case class BusinessSpecificationsPartial(
  userId: String,
  businessId: String,
  businessName: Option[String],
  description: Option[String],
  openingHours: Option[List[OpeningHours]]
)

object BusinessSpecificationsPartial {

  implicit val encoder: Encoder[BusinessSpecificationsPartial] = deriveEncoder[BusinessSpecificationsPartial]
  implicit val decoder: Decoder[BusinessSpecificationsPartial] = deriveDecoder[BusinessSpecificationsPartial]
}
