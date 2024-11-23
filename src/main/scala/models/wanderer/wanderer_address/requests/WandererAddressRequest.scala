package models.wanderer.wanderer_address.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime

case class WandererAddressRequest(
                                   userId: String,
                                   street: String,
                                   city: String,
                                   country: String,
                                   county: Option[String],
                                   postcode: String,
                                   createdAt: LocalDateTime,
                                   updated_at: LocalDateTime
                                 )

object WandererAddressRequest {
  implicit val wandererAddressRequestEncoder: Encoder[WandererAddressRequest] = deriveEncoder[WandererAddressRequest]
  implicit val wandererAddressRequestDecoder: Decoder[WandererAddressRequest] = deriveDecoder[WandererAddressRequest]
}

