package models.business.business_contact_details

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class BusinessContactDetails(
                                   id: Option[Int],
                                   userId: String,
                                   businessId: String,
                                   businessName: String,
                                   primaryContactFirstName: String,
                                   primaryContactLastName: String,
                                   contactEmail: String,
                                   contactNumber: String,
                                   websiteUrl: String,
                                   createdAt: LocalDateTime,
                                   updatedAt: LocalDateTime
                                 )

object BusinessContactDetails {
  implicit val businessContactDetailsEncoder: Encoder[BusinessContactDetails] = deriveEncoder[BusinessContactDetails]
  implicit val businessContactDetailsDecoder: Decoder[BusinessContactDetails] = deriveDecoder[BusinessContactDetails]
}
