package models.office.specifications.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

case class UpdateOfficeSpecificationsRequest(
  officeName: String,
  description: String,
  officeType: OfficeType,
  numberOfFloors: Int,
  totalDesks: Int,
  capacity: Int,
  amenities: List[String],
  availability: OfficeAvailability,
  rules: Option[String]
)

object UpdateOfficeSpecificationsRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateOfficeSpecificationsRequest] = deriveEncoder[UpdateOfficeSpecificationsRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateOfficeSpecificationsRequest] = deriveDecoder[UpdateOfficeSpecificationsRequest]
}
