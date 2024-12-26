package models.business.specifications.requests

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}
import models.business.specifications.BusinessAvailability

import java.time.LocalDateTime

case class CreateBusinessSpecificationsRequest(
                                                userId: String,
                                                businessId: String,
                                                businessName: String,
                                                description: String,
                                              )

object CreateBusinessSpecificationsRequest {
  implicit val createBusinessSpecificationsRequestEncoder: Encoder[CreateBusinessSpecificationsRequest] = deriveEncoder[CreateBusinessSpecificationsRequest]
  implicit val createBusinessSpecificationsRequestDecoder: Decoder[CreateBusinessSpecificationsRequest] = deriveDecoder[CreateBusinessSpecificationsRequest]
}

