package models.business.business_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.business.address.BusinessAddress
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.BusinessSpecifications

case class BusinessListing(businessId: String, addressDetails: BusinessAddress, businessContactDetails: BusinessContactDetails, businessSpecs: BusinessSpecifications)

object BusinessListing {
  implicit val businessListingEncoder: Encoder[BusinessListing] = deriveEncoder[BusinessListing]
  implicit val businessListingDecoder: Decoder[BusinessListing] = deriveDecoder[BusinessListing]
}
