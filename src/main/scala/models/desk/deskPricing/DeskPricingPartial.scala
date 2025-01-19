package models.desk.deskPricing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class DeskPricingPartial(
  pricePerHour: BigDecimal,
  pricePerDay: Option[BigDecimal],
  pricePerWeek: Option[BigDecimal],
  pricePerMonth: Option[BigDecimal],
  pricePerYear: Option[BigDecimal]
)

object DeskPricingPartial {
  implicit val encoder: Encoder[DeskPricingPartial] = deriveEncoder[DeskPricingPartial]
  implicit val decoder: Decoder[DeskPricingPartial] = deriveDecoder[DeskPricingPartial]
}
