package models.business.business_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class BusinessListingCard(businessId: String, businessName: String, description: String)

object BusinessListingCard {
  implicit val businessListingCardEncoder: Encoder[BusinessListingCard] = deriveEncoder[BusinessListingCard]
  implicit val businessListingCardDecoder: Decoder[BusinessListingCard] = deriveDecoder[BusinessListingCard]
}
