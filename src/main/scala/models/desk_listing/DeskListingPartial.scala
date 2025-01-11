package models.desk_listing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.desk_listing.Availability
import models.desk_listing.DeskType

case class DeskListingPartial(
  deskName: String,
  description: Option[String],
  deskType: DeskType,
  quantity: Int,
  pricePerHour: BigDecimal,
  pricePerDay: BigDecimal,
  features: List[String],
  availability: Availability,
  rules: Option[String]
)

object DeskListingPartial {
  implicit val encoder: Encoder[DeskListingPartial] = deriveEncoder[DeskListingPartial]
  implicit val decoder: Decoder[DeskListingPartial] = deriveDecoder[DeskListingPartial]
}
