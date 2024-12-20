package models.office.office_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.office.address_details.OfficeAddress
import models.office.specifications.{OfficeAvailability, OfficeSpecs}

import java.time.LocalDateTime

case class OfficeListing(
                          id: Option[Int],
                          officeId: String,
                          officeSpecs: OfficeSpecs,
                          addressDetails: OfficeAddress,
                          availability: OfficeAvailability,
                          createdAt: LocalDateTime,
                          updatedAt: LocalDateTime
                        )

object OfficeListing {
  implicit val officeListingRequestEncoder: Encoder[OfficeListing] = deriveEncoder[OfficeListing]
  implicit val officeListingRequestDecoder: Decoder[OfficeListing] = deriveDecoder[OfficeListing]
}

