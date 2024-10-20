package models.workspaces

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class Workspace(
                      id: Option[Int],
                      business_id: String,
                      workspace_id: String,
                      name: String,
                      description: String,
                      address: String,
                      city: String,
                      country: String,
                      postcode: String,
                      price_per_day: BigDecimal,
                      latitude: BigDecimal,
                      longitude: BigDecimal,
                      created_at: LocalDateTime
                    )

object Workspace {
  implicit val businessEncoder: Encoder[Workspace] = deriveEncoder[Workspace]
  implicit val businessDecoder: Decoder[Workspace] = deriveDecoder[Workspace]
}
