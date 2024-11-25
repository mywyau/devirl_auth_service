package models.office.office_details

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class OfficeDetails(
                          id: Option[Int],
                          businessId: String,
                          office_name: String,
                          description: String,
                          office_type: String,
                          capacity: Int,
                          numberOfFloors: Int,
                          amenities: List[String],
                          rules: Option[String],
                          createdAt: LocalDateTime,
                          updatedAt: LocalDateTime
                        )

object OfficeDetails {
  implicit val officeAddressEncoder: Encoder[OfficeDetails] = deriveEncoder[OfficeDetails]
  implicit val officeAddressDecoder: Decoder[OfficeDetails] = deriveDecoder[OfficeDetails]
}
