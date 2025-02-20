package models.business.address

import io.circe.generic.semiauto.deriveDecoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.Decoder
import io.circe.Encoder
import java.time.LocalDateTime
import models.business.address.UpdateBusinessAddressRequest

case class UpdateBusinessAddressRequest(
  buildingName: Option[String],
  floorNumber: Option[String],
  street: String,
  city: String,
  country: String,
  county: String,
  postcode: String,
  latitude: BigDecimal,
  longitude: BigDecimal
)

object UpdateBusinessAddressRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateBusinessAddressRequest] = deriveEncoder[UpdateBusinessAddressRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateBusinessAddressRequest] = deriveDecoder[UpdateBusinessAddressRequest]
}
