package models.workspaces

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class Workspace(
                      id: Option[Int],
                      businessId: String,
                      workspaceId: String,
                      name: String,
                      description: String,
                      address: String,
                      city: String,
                      country: String,
                      postcode: String,
                      pricePerDay: BigDecimal,
                      latitude: BigDecimal,
                      longitude: BigDecimal,
                      createdAt: LocalDateTime
                    )

object Workspace {
  implicit val businessEncoder: Encoder[Workspace] = deriveEncoder[Workspace]
  implicit val businessDecoder: Decoder[Workspace] = deriveDecoder[Workspace]
}
