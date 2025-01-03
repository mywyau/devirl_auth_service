package models.office.office_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications

case class OfficeListingRequest(
  officeId: String,
  createOfficeAddressRequest: CreateOfficeAddressRequest,
  createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest,
  createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest,
  createdAt: LocalDateTime,
  updatedAt: LocalDateTime
)

object OfficeListingRequest {
  implicit val officeListingRequestEncoder: Encoder[OfficeListingRequest] = deriveEncoder[OfficeListingRequest]
  implicit val officeListingRequestDecoder: Decoder[OfficeListingRequest] = deriveDecoder[OfficeListingRequest]
}
