package models.wanderer.wanderer_address.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class WandererAddress(
                            id: Option[Int],
                            userId: String,
                            street: Option[String],
                            city: Option[String],
                            country: Option[String],
                            county: Option[String],
                            postcode: Option[String],
                            createdAt: LocalDateTime,
                            updatedAt: LocalDateTime
                          )

object WandererAddress {
  implicit val wandererAddressEncoder: Encoder[WandererAddress] = deriveEncoder[WandererAddress]
  implicit val wandererAddressDecoder: Decoder[WandererAddress] = deriveDecoder[WandererAddress]
}
