package models.office.office_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

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

