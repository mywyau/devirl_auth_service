package models.office.specifications.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

import java.time.LocalDateTime


case class UpdateOfficeSpecificationsRequest(
                                              officeName: String,
                                              description: String,
                                              officeType: OfficeType,
                                              numberOfFloors: Int,
                                              totalDesks: Int,
                                              capacity: Int,
                                              amenities: List[String],
                                              availability: OfficeAvailability,
                                              rules: Option[String],
                                              updatedAt: LocalDateTime
                                            )

object UpdateOfficeSpecificationsRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateOfficeSpecificationsRequest] = deriveEncoder[UpdateOfficeSpecificationsRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateOfficeSpecificationsRequest] = deriveDecoder[UpdateOfficeSpecificationsRequest]
}
