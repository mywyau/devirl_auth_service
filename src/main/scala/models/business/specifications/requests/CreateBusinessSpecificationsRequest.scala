package models.business.specifications.requests

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.specifications.BusinessAvailability

case class CreateBusinessSpecificationsRequest(
  userId: String,
  businessId: String,
  businessName: String,
  description: String,
  availability: BusinessAvailability
)

object CreateBusinessSpecificationsRequest {
  implicit val createBusinessSpecificationsRequestEncoder: Encoder[CreateBusinessSpecificationsRequest] = deriveEncoder[CreateBusinessSpecificationsRequest]
  implicit val createBusinessSpecificationsRequestDecoder: Decoder[CreateBusinessSpecificationsRequest] = deriveDecoder[CreateBusinessSpecificationsRequest]
}
