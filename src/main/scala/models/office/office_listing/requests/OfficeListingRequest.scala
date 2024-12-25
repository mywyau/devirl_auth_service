package models.office.office_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import models.office.contact_details.OfficeContactDetails

import java.time.LocalDateTime

case class OfficeListingRequest(
                                 officeId: String,
                                 addressDetails: CreateOfficeAddressRequest,
                                 officeSpecs: OfficeSpecifications,
                                 contactDetails: OfficeContactDetails,
                                 createdAt: LocalDateTime,
                                 updatedAt: LocalDateTime
                               )

object OfficeListingRequest {
  implicit val officeListingRequestEncoder: Encoder[OfficeListingRequest] = deriveEncoder[OfficeListingRequest]
  implicit val officeListingRequestDecoder: Decoder[OfficeListingRequest] = deriveDecoder[OfficeListingRequest]
}

