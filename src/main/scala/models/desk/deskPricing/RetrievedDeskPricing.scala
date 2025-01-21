package models.desk.deskPricing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder

case class RetrievedDeskPricing(
  pricePerHour: Option[BigDecimal],
  pricePerDay: Option[BigDecimal],
  pricePerWeek: Option[BigDecimal],
  pricePerMonth: Option[BigDecimal],
  pricePerYear: Option[BigDecimal]
)

object RetrievedDeskPricing {
  implicit val encoder: Encoder[RetrievedDeskPricing] = deriveEncoder[RetrievedDeskPricing]
  implicit val decoder: Decoder[RetrievedDeskPricing] = deriveDecoder[RetrievedDeskPricing]
}
