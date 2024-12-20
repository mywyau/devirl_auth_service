package models.office.office_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.office.address_details.OfficeAddress
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecs
import models.office.contact_details.OfficeContactDetails

import java.time.LocalDateTime

case class OfficeListingRequest(
                                 officeId: String,
                                 addressDetails: OfficeAddress,
                                 officeSpecs: OfficeSpecs,
                                 contactDetails: OfficeContactDetails,
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )

object OfficeListingRequest {
  implicit val officeListingRequestEncoder: Encoder[OfficeListingRequest] = deriveEncoder[OfficeListingRequest]
  implicit val officeListingRequestDecoder: Decoder[OfficeListingRequest] = deriveDecoder[OfficeListingRequest]
}

