package models.office_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json
import java.time.LocalDateTime
import models.desk_listing.DeskType
import models.desk_listing.Availability
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications

case class InitiateOfficeListingRequest(
  businessId: String,
  officeId: String,
  officeName: String,
  description: String
)

object InitiateOfficeListingRequest {
  implicit val initiateOfficeListingRequestEncoder: Encoder[InitiateOfficeListingRequest] = deriveEncoder[InitiateOfficeListingRequest]
  implicit val initiateOfficeListingRequestDecoder: Decoder[InitiateOfficeListingRequest] = deriveDecoder[InitiateOfficeListingRequest]
}
