package models.office.specifications.requests

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import models.office.adts.OfficeType


import java.time.LocalDateTime
import models.OpeningHours

case class UpdateOfficeSpecificationsRequest(
  officeName: String,
  description: String,
  officeType: OfficeType,
  numberOfFloors: Int,
  totalDesks: Int,
  capacity: Int,
  amenities: List[String],
  openingHours: List[OpeningHours],
  rules: Option[String]
)

object UpdateOfficeSpecificationsRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateOfficeSpecificationsRequest] = deriveEncoder[UpdateOfficeSpecificationsRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateOfficeSpecificationsRequest] = deriveDecoder[UpdateOfficeSpecificationsRequest]
}
