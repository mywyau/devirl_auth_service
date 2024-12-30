package models.office.office_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.office.address_details.OfficeAddress
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.OfficeSpecifications

case class OfficeListing(
                          officeId: String,
                          officeAddressDetails: OfficeAddress,
                          officeContactDetails: OfficeContactDetails,
                          officeSpecifications: OfficeSpecifications
                        )

object OfficeListing {
  implicit val officeListingRequestEncoder: Encoder[OfficeListing] = deriveEncoder[OfficeListing]
  implicit val officeListingRequestDecoder: Decoder[OfficeListing] = deriveDecoder[OfficeListing]
}

