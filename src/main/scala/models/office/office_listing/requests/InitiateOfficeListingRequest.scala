package models.office.office_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.LocalDateTime

case class InitiateOfficeListingRequest(
                                           businessId: String,
                                           officeId: String,
                                           officeName: String,
                                           description: String,
                                         )

object InitiateOfficeListingRequest {
  implicit val initiateOfficeListingRequestEncoder: Encoder[InitiateOfficeListingRequest] = deriveEncoder[InitiateOfficeListingRequest]
  implicit val initiateOfficeListingRequestDecoder: Decoder[InitiateOfficeListingRequest] = deriveDecoder[InitiateOfficeListingRequest]
}

