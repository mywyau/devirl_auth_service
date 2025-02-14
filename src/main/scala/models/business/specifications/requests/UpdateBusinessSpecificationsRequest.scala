package models.business.specifications.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.OpeningHours

case class UpdateBusinessSpecificationsRequest(
  businessName: String,
  description: String,
  openingHours: List[OpeningHours]
)

object UpdateBusinessSpecificationsRequest {
  implicit val updateBusinessSpecificationsRequestEncoder: Encoder[UpdateBusinessSpecificationsRequest] = deriveEncoder[UpdateBusinessSpecificationsRequest]
  implicit val updateBusinessSpecificationsRequestDecoder: Decoder[UpdateBusinessSpecificationsRequest] = deriveDecoder[UpdateBusinessSpecificationsRequest]
}
