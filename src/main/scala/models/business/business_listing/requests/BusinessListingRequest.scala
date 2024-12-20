package models.business.business_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.adts.DeskType
import models.business.desk_listing.Availability
import models.business.address_details.requests.BusinessAddressRequest
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications
import models.business.contact_details.BusinessContactDetails

import java.time.LocalDateTime

case class BusinessListingRequest(
                                   businessId: String,
                                   addressDetails: BusinessAddressRequest,
                                   businessSpecs: BusinessSpecifications,
                                   contactDetails: BusinessContactDetails,
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                               )

object BusinessListingRequest {
  implicit val businessListingRequestEncoder: Encoder[BusinessListingRequest] = deriveEncoder[BusinessListingRequest]
  implicit val businessListingRequestDecoder: Decoder[BusinessListingRequest] = deriveDecoder[BusinessListingRequest]
}

