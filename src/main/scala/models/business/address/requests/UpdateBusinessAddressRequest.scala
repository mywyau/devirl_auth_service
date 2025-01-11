package models.business.address.requests

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime

case class UpdateBusinessAddressRequest(
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

object UpdateBusinessAddressRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateBusinessAddressRequest] = deriveEncoder[UpdateBusinessAddressRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateBusinessAddressRequest] = deriveDecoder[UpdateBusinessAddressRequest]
}
