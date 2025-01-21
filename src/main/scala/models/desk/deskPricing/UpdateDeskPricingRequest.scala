package models.desk.deskPricing

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import io.circe.Json

case class UpdateDeskPricingRequest(
  pricePerHour: BigDecimal,
  pricePerDay: Option[BigDecimal],
  pricePerWeek: Option[BigDecimal],
  pricePerMonth: Option[BigDecimal],
  pricePerYear: Option[BigDecimal]
)

object UpdateDeskPricingRequest {
  implicit val encoder: Encoder[UpdateDeskPricingRequest] = deriveEncoder[UpdateDeskPricingRequest]
  implicit val decoder: Decoder[UpdateDeskPricingRequest] = deriveDecoder[UpdateDeskPricingRequest]
}
