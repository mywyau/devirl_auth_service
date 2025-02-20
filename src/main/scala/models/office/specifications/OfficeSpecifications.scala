package models.office.specifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.OpeningHours
import models.office.OfficeType


case class OfficeSpecifications(
  id: Option[Int],
  businessId: String,
  officeId: String,
  officeName: Option[String],
  description: Option[String],
  officeType: Option[OfficeType],
  numberOfFloors: Option[Int],
  totalDesks: Option[Int],
  capacity: Option[Int],
  amenities: Option[List[String]],
  openingHours: Option[List[OpeningHours]],
  rules: Option[String],
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object OfficeSpecifications {
  implicit val officeSpecificationsEncoder: Encoder[OfficeSpecifications] = deriveEncoder[OfficeSpecifications]
  implicit val officeSpecificationsDecoder: Decoder[OfficeSpecifications] = deriveDecoder[OfficeSpecifications]
}
