package models.desk.deskListing

import io.circe.Decoder
import io.circe.Encoder
import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.DeskSpecificationsPartial

case class DeskListingBusinessAndOffice(
  deskId: String,
  officeId: String,
  businessId: String
)

object DeskListingBusinessAndOffice {
  implicit val encoder: Encoder[DeskListingBusinessAndOffice] = deriveEncoder[DeskListingBusinessAndOffice]
  implicit val decoder: Decoder[DeskListingBusinessAndOffice] = deriveDecoder[DeskListingBusinessAndOffice]
}
