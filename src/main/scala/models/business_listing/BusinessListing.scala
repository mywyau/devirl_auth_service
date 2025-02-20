package models.business_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.business.address.BusinessAddressPartial
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.specifications.BusinessSpecificationsPartial
import models.business.address.BusinessAddressPartial

case class BusinessListing(
  userId: String,
  businessId: String,
  addressDetails: BusinessAddressPartial,
  contactDetails: BusinessContactDetailsPartial,
  specifications: BusinessSpecificationsPartial
)

object BusinessListing {
  implicit val businessListingEncoder: Encoder[BusinessListing] = deriveEncoder[BusinessListing]
  implicit val businessListingDecoder: Decoder[BusinessListing] = deriveDecoder[BusinessListing]
}
