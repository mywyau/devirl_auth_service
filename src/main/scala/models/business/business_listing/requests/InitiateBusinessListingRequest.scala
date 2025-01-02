package models.business.business_listing.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class InitiateBusinessListingRequest(
                                           userId: String,
                                           businessId: String,
                                           businessName: String,
                                           description: String
                                         )

object InitiateBusinessListingRequest {
  implicit val initiateBusinessListingRequestEncoder: Encoder[InitiateBusinessListingRequest] = deriveEncoder[InitiateBusinessListingRequest]
  implicit val initiateBusinessListingRequestDecoder: Decoder[InitiateBusinessListingRequest] = deriveDecoder[InitiateBusinessListingRequest]
}

