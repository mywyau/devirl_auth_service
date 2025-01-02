package models.business.contact_details

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class BusinessContactDetails(
                                   id: Option[Int],
                                   userId: String,
                                   businessId: String,
                                   businessName: Option[String],
                                   primaryContactFirstName: Option[String],
                                   primaryContactLastName: Option[String],
                                   contactEmail: Option[String],
                                   contactNumber: Option[String],
                                   websiteUrl: Option[String],
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessContactDetails {
  implicit val businessContactDetailsEncoder: Encoder[BusinessContactDetails] = deriveEncoder[BusinessContactDetails]
  implicit val businessContactDetailsDecoder: Decoder[BusinessContactDetails] = deriveDecoder[BusinessContactDetails]
}
