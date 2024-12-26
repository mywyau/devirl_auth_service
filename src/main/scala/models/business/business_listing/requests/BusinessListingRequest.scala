package models.business.business_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder, Json}
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.requests.CreateBusinessSpecificationsRequest

import java.time.LocalDateTime

case class BusinessListingRequest(
                                   businessId: String,
                                   addressDetails: CreateBusinessAddressRequest,
                                   businessSpecs: CreateBusinessSpecificationsRequest,
                                   contactDetails: CreateBusinessContactDetailsRequest,
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessListingRequest {
  implicit val businessListingRequestEncoder: Encoder[BusinessListingRequest] = deriveEncoder[BusinessListingRequest]
  implicit val businessListingRequestDecoder: Decoder[BusinessListingRequest] = deriveDecoder[BusinessListingRequest]
}

