package models.business.specifications.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.specifications.BusinessAvailability

case class UpdateBusinessSpecificationsRequest(
  businessName: String,
  description: String,
  availability: BusinessAvailability
)

object UpdateBusinessSpecificationsRequest {
  implicit val updateBusinessSpecificationsRequestEncoder: Encoder[UpdateBusinessSpecificationsRequest] = deriveEncoder[UpdateBusinessSpecificationsRequest]
  implicit val updateBusinessSpecificationsRequestDecoder: Decoder[UpdateBusinessSpecificationsRequest] = deriveDecoder[UpdateBusinessSpecificationsRequest]
}
