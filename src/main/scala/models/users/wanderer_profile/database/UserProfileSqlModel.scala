package models.users.wanderer_profile.database

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime


case class UserProfileSqlModel(
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
                                    created_at: LocalDateTime,
                                    updated_at: LocalDateTime
                                  )

object UserProfileSqlModel {
  implicit val userProfileSqlRetrievalEncoder: Encoder[UserProfileSqlModel] = deriveEncoder[UserProfileSqlModel]
  implicit val userProfileSqlRetrievalDecoder: Decoder[UserProfileSqlModel] = deriveDecoder[UserProfileSqlModel]
}
