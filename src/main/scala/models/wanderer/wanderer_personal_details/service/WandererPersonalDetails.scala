package models.wanderer.wanderer_personal_details.service

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class WandererPersonalDetails(
                                    id: Option[Int],
                                    userId: String,
                                    firstName: Option[String],
                                    lastName: Option[String],
                                    contactNumber: Option[String],
                                    email: Option[String],
                                    company: Option[String],
                                    createdAt: LocalDateTime,
                                    updatedAt: LocalDateTime
                                  )

object WandererPersonalDetails {
  implicit val wandererContactDetailsEncoder: Encoder[WandererPersonalDetails] = deriveEncoder[WandererPersonalDetails]
  implicit val wandererContactDetailsDecoder: Decoder[WandererPersonalDetails] = deriveDecoder[WandererPersonalDetails]
}
