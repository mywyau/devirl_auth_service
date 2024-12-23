package models.office.specifications

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.office.adts.OfficeType
import models.office.specifications.OfficeAvailability

import java.time.LocalDateTime

case class OfficeSpecifications(
                                 id: Option[Int],
                                 businessId: String,
                                 officeId: String,
                                 officeName: String,
                                 description: String,
                                 officeType: OfficeType,
                                 numberOfFloors: Int,
                                 totalDesks: Int,
                                 capacity: Int,
                                 amenities: List[String],
                                 availability: OfficeAvailability,
                                 rules: Option[String],
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )

object OfficeSpecifications {
  implicit val officeSpecificationsEncoder: Encoder[OfficeSpecifications] = deriveEncoder[OfficeSpecifications]
  implicit val officeSpecificationsDecoder: Decoder[OfficeSpecifications] = deriveDecoder[OfficeSpecifications]
}
