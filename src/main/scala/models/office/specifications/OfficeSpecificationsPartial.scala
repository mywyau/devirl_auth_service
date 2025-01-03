package models.office.specifications

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

case class OfficeSpecificationsPartial(
  businessId: String,
  officeId: String,
  officeName: Option[String],
  description: Option[String],
  officeType: Option[OfficeType],
  numberOfFloors: Option[Int],
  totalDesks: Option[Int],
  capacity: Option[Int],
  amenities: Option[List[String]],
  availability: Option[OfficeAvailability],
  rules: Option[String]
)

object OfficeSpecificationsPartial {
  implicit val encoder: Encoder[OfficeSpecificationsPartial] = deriveEncoder[OfficeSpecificationsPartial]
  implicit val decoder: Decoder[OfficeSpecificationsPartial] = deriveDecoder[OfficeSpecificationsPartial]
}
