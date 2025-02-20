package models.business.address

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.address.BusinessAddressPartial

case class BusinessAddressPartial(
  userId: String,
  businessId: String,
  buildingName: Option[String],
  floorNumber: Option[String],
  street: Option[String],
  city: Option[String],
  country: Option[String],
  county: Option[String],
  postcode: Option[String],
  latitude: Option[BigDecimal],
  longitude: Option[BigDecimal]
)

object BusinessAddressPartial {
  implicit val encoder: Encoder[BusinessAddressPartial] = deriveEncoder[BusinessAddressPartial]
  implicit val decoder: Decoder[BusinessAddressPartial] = deriveDecoder[BusinessAddressPartial]
}
