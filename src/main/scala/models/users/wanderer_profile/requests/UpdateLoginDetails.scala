package models.users.wanderer_profile.requests

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role


case class UpdateLoginDetails(
                               username: Option[String],
                               passwordHash: Option[String],
                               email: Option[String],
                               role: Option[Role]
                             )

object UpdateLoginDetails {
  implicit val updateLoginDetailsRequestEncoder: Encoder[UpdateLoginDetails] = deriveEncoder[UpdateLoginDetails]
  implicit val updateLoginDetailsRequestDecoder: Decoder[UpdateLoginDetails] = deriveDecoder[UpdateLoginDetails]
}

case class UpdateAddress(
                          street: Option[String],
                          city: Option[String],
                          country: Option[String],
                          county: Option[String],
                          postcode: Option[String]
                        )

object UpdateAddress {
  implicit val updateAddressRequestEncoder: Encoder[UpdateAddress] = deriveEncoder[UpdateAddress]
  implicit val updateAddressRequestDecoder: Decoder[UpdateAddress] = deriveDecoder[UpdateAddress]
}


case class UpdatePersonalDetails(
                                  firstName: Option[String],
                                  lastName: Option[String],
                                  contactNumber: Option[String],
                                  email: Option[String],
                                  company: Option[String]
                                )

object UpdatePersonalDetails {
  implicit val updatePersonalDetailsRequestEncoder: Encoder[UpdatePersonalDetails] = deriveEncoder[UpdatePersonalDetails]
  implicit val updatePersonalDetailsRequestDecoder: Decoder[UpdatePersonalDetails] = deriveDecoder[UpdatePersonalDetails]
}

case class UpdateProfileRequest(
                                 loginDetails: Option[UpdateLoginDetails],
                                 address: Option[UpdateAddress],
                                 personalDetails: Option[UpdatePersonalDetails]
                               )


object UpdateProfileRequest {
  implicit val updateProfileRequestRequestEncoder: Encoder[UpdateProfileRequest] = deriveEncoder[UpdateProfileRequest]
  implicit val updateProfileRequestRequestDecoder: Decoder[UpdateProfileRequest] = deriveDecoder[UpdateProfileRequest]
}