package models.desk.deskListing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing

case class DeskListing(
  deskId: String,
  specifications: DeskSpecificationsPartial,
  pricing: RetrievedDeskPricing
)

object DeskListing {
  implicit val encoder: Encoder[DeskListing] = deriveEncoder[DeskListing]
  implicit val decoder: Decoder[DeskListing] = deriveDecoder[DeskListing]
}
