package models.office_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class OfficeListingCard(
  businessId: String,
  officeId: String,
  officeName: String,
  description: String
)

object OfficeListingCard {
  implicit val officeListingCardEncoder: Encoder[OfficeListingCard] = deriveEncoder[OfficeListingCard]
  implicit val officeListingCardDecoder: Decoder[OfficeListingCard] = deriveDecoder[OfficeListingCard]
}
