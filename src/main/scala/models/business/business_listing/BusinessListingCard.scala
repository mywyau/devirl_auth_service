package models.business.business_listing

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class BusinessListingCard(
                                businessId: String,
                                businessName: String,
                                description: String,
                              )

object BusinessListingCard {
  implicit val businessListingCardEncoder: Encoder[BusinessListingCard] = deriveEncoder[BusinessListingCard]
  implicit val businessListingCardDecoder: Decoder[BusinessListingCard] = deriveDecoder[BusinessListingCard]
}

