package models.office.contact_details

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class OfficeContactDetails(
                                 id: Option[Int],
                                 businessId: String,
                                 officeId: String,
                                 primaryContactFirstName: String,
                                 primaryContactLastName: String,
                                 contactEmail: String,
                                 contactNumber: String,
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )

object OfficeContactDetails {
  implicit val officeContactDetailsEncoder: Encoder[OfficeContactDetails] = deriveEncoder[OfficeContactDetails]
  implicit val officeContactDetailsDecoder: Decoder[OfficeContactDetails] = deriveDecoder[OfficeContactDetails]
}
