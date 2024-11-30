package models.office.office_specs

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.office.office_specs.OfficeAvailability
import models.office.adts.OfficeType

import java.time.LocalDateTime

case class OfficeSpecs(
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

object OfficeSpecs {
  implicit val officeSpecsEncoder: Encoder[OfficeSpecs] = deriveEncoder[OfficeSpecs]
  implicit val officeSpecsDecoder: Decoder[OfficeSpecs] = deriveDecoder[OfficeSpecs]
}
