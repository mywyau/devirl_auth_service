package models.office.office_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.office.address_details.OfficeAddress
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.OfficeSpecificationsPartial
import models.office.address_details.OfficeAddressPartial
import models.office.contact_details.OfficeContactDetailsPartial

case class OfficeListing(
  officeId: String,
  officeAddressDetails: OfficeAddressPartial,
  officeContactDetails: OfficeContactDetailsPartial,
  officeSpecifications: OfficeSpecificationsPartial
)

object OfficeListing {
  implicit val officeListingRequestEncoder: Encoder[OfficeListing] = deriveEncoder[OfficeListing]
  implicit val officeListingRequestDecoder: Decoder[OfficeListing] = deriveDecoder[OfficeListing]
}
