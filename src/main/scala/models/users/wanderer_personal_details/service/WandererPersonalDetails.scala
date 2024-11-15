package models.users.wanderer_personal_details.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class WandererPersonalDetails(
                                    id: Option[Int],
                                    user_id: String,
                                    first_name: String,
                                    last_name: String,
                                    contact_number: String,
                                    email: String,
                                    company: String,
                                    created_at: LocalDateTime,
                                    updated_at: LocalDateTime
                                  )

object WandererPersonalDetails {
  implicit val wandererContactDetailsEncoder: Encoder[WandererPersonalDetails] = deriveEncoder[WandererPersonalDetails]
  implicit val wandererContactDetailsDecoder: Decoder[WandererPersonalDetails] = deriveDecoder[WandererPersonalDetails]
}
