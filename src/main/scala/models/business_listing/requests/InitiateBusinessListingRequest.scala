package models.business_listing.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

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
