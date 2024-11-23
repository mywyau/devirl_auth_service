package models.wanderer.wanderer_personal_details.repository

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class WandererPersonalDetailsUpdate(
                                          id: Option[Int],
                                          userId: String,
                                          firstName: String,
                                          lastName: String,
                                          contactNumber: String,
                                          email: String,
                                          company: String,
                                          createdAt: LocalDateTime,
                                          updatedAt: LocalDateTime
                                        )

object WandererPersonalDetailsUpdate {
  implicit val wandererPersonalDetailsUpdateEncoder: Encoder[WandererPersonalDetailsUpdate] = deriveEncoder[WandererPersonalDetailsUpdate]
  implicit val wandererPersonalDetailsUpdateDecoder: Decoder[WandererPersonalDetailsUpdate] = deriveDecoder[WandererPersonalDetailsUpdate]
}
