package models.business.contact_details.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime


case class UpdateBusinessContactDetailsRequest(
                                                businessName: String,
                                                primaryContactFirstName: String,
                                                primaryContactLastName: String,
                                                contactEmail: String,
                                                contactNumber: String,
                                                websiteUrl: String,
                                                updatedAt: LocalDateTime
                                              )

object UpdateBusinessContactDetailsRequest {
  implicit val createOfficeAddressRequestEncoder: Encoder[UpdateBusinessContactDetailsRequest] = deriveEncoder[UpdateBusinessContactDetailsRequest]
  implicit val createOfficeAddressRequestDecoder: Decoder[UpdateBusinessContactDetailsRequest] = deriveDecoder[UpdateBusinessContactDetailsRequest]
}
