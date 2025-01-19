package models.desk.deskListing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class DeskListingCard(
  deskId: String,
  deskName: String,
  description: String
)

object DeskListingCard {
  implicit val encoder: Encoder[DeskListingCard] = deriveEncoder[DeskListingCard]
  implicit val decoder: Decoder[DeskListingCard] = deriveDecoder[DeskListingCard]
}
