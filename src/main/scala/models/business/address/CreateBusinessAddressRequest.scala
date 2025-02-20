package models.business.address

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.address.CreateBusinessAddressRequest

case class CreateBusinessAddressRequest(
  userId: String,
  businessId: String,
  businessName: Option[String],
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

object CreateBusinessAddressRequest {
  implicit val createBusinessAddressRequestEncoder: Encoder[CreateBusinessAddressRequest] = deriveEncoder[CreateBusinessAddressRequest]
  implicit val createBusinessAddressRequestDecoder: Decoder[CreateBusinessAddressRequest] = deriveDecoder[CreateBusinessAddressRequest]
}
