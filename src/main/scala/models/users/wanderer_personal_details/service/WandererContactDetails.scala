package models.users.wanderer_personal_details.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime


case class WandererContactDetails(
                                   id: Option[Int],
                                   user_id: String,
                                   contact_number: String,
                                   email: String,
                                   created_at: LocalDateTime,
                                   updated_at: LocalDateTime
                                 )

object WandererContactDetails {
  implicit val wandererContactDetailsEncoder: Encoder[WandererContactDetails] = deriveEncoder[WandererContactDetails]
  implicit val wandererContactDetailsDecoder: Decoder[WandererContactDetails] = deriveDecoder[WandererContactDetails]
}
