package models.office.office_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.office.address_details.OfficeAddress
import models.office.contact_details.OfficeContactDetails
import models.office.specifications.OfficeSpecifications

case class OfficeListingCard(
                              businessId: String,
                              officeId: String,
                              officeName: String,
                              description: String,
                            )

object OfficeListingCard {
  implicit val officeListingCardEncoder: Encoder[OfficeListingCard] = deriveEncoder[OfficeListingCard]
  implicit val officeListingCardDecoder: Decoder[OfficeListingCard] = deriveDecoder[OfficeListingCard]
}

