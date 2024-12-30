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
                                 officeName: Option[String],
                                 description: Option[String],
                                 officeType: Option[OfficeType],
                                 numberOfFloors: Option[Int],
                                 totalDesks: Option[Int],
                                 capacity: Option[Int],
                                 amenities: Option[List[String]],
                                 availability: Option[OfficeAvailability],
                                 rules: Option[String],
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )


object OfficeSpecifications {
  implicit val officeSpecificationsEncoder: Encoder[OfficeSpecifications] = deriveEncoder[OfficeSpecifications]
  implicit val officeSpecificationsDecoder: Decoder[OfficeSpecifications] = deriveDecoder[OfficeSpecifications]
}
