package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

case class UserProfile(
                        userId: String,
                        userLoginDetails: UserLoginDetails,
                        first_name: String,
                        last_name: String,
                        userAddress: UserAddress,
                        contact_number: String,
                        email: String,
                        role: Role,
                        created_at: LocalDateTime
                      )

object UserProfile {
  implicit val userProfileEncoder: Encoder[UserProfile] = deriveEncoder[UserProfile]
  implicit val userProfileDecoder: Decoder[UserProfile] = deriveDecoder[UserProfile]
}


case class UserProfileSqlRetrieval(
                                    id: Int,
                                    userId: String,
                                    username: String,
                                    password_hash: String,
                                    first_name: String,
                                    last_name: String,
                                    street: String,
                                    city: String,
                                    country: String,
                                    county: Option[String], // Adjusted to Option for nullable fields
                                    postcode: String,
                                    contact_number: String,
                                    email: String,
                                    role: Role,
                                    created_at: LocalDateTime
                                  )

object UserProfileSqlRetrieval {
  implicit val userProfileSqlRetrievalEncoder: Encoder[UserProfileSqlRetrieval] = deriveEncoder[UserProfileSqlRetrieval]
  implicit val userProfileSqlRetrievalDecoder: Decoder[UserProfileSqlRetrieval] = deriveDecoder[UserProfileSqlRetrieval]
}
