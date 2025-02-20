package models.office.specifications

import doobie.util.meta.Meta
import io.circe.generic.semiauto.*
import io.circe.parser.decode
import io.circe.syntax.*
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.office.OfficeType
import models.OpeningHours

case class CreateOfficeSpecificationsRequest(
  businessId: String,
  officeId: String,
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

object CreateOfficeSpecificationsRequest {
  implicit val createOfficeSpecificationsRequestEncoder: Encoder[CreateOfficeSpecificationsRequest] = deriveEncoder[CreateOfficeSpecificationsRequest]
  implicit val createOfficeSpecificationsRequestDecoder: Decoder[CreateOfficeSpecificationsRequest] = deriveDecoder[CreateOfficeSpecificationsRequest]
}
