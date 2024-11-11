package models.users.wanderer_address.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime


case class WandererAddress(
                               id: Option[Int],
                               user_id: String,
                               street: String,
                               city: String,
                               country: String,
                               county: Option[String],
                               postcode: String,
                               created_at: LocalDateTime,
                               updated_at: LocalDateTime
                             )

object WandererAddress {
  implicit val wandererAddressEncoder: Encoder[WandererAddress] = deriveEncoder[WandererAddress]
  implicit val wandererAddressDecoder: Decoder[WandererAddress] = deriveDecoder[WandererAddress]
}
