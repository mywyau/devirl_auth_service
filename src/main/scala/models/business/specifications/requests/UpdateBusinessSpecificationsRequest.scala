package models.business.specifications.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.business.specifications.BusinessAvailability

import java.time.LocalDateTime


case class UpdateBusinessSpecificationsRequest(
                                                businessName: String,
                                                description: String,
                                                availability: BusinessAvailability,
                                                updatedAt: LocalDateTime
                                              )

object UpdateBusinessSpecificationsRequest {
  implicit val updateBusinessSpecificationsRequestEncoder: Encoder[UpdateBusinessSpecificationsRequest] = deriveEncoder[UpdateBusinessSpecificationsRequest]
  implicit val updateBusinessSpecificationsRequestDecoder: Decoder[UpdateBusinessSpecificationsRequest] = deriveDecoder[UpdateBusinessSpecificationsRequest]
}
