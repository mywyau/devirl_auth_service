package models.users.wanderer_profile.database

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime


case class UserProfileSqlModel(
                                id: Int,
                                userId: String,
                                username: String,
                                passwordHash: String,
                                firstName: String,
                                lastName: String,
                                street: String,
                                city: String,
                                country: String,
                                county: Option[String],
                                postcode: String,
                                contactNumber: String,
                                email: String,
                                role: Role,
                                createdAt: LocalDateTime,
                                updatedAt: LocalDateTime
                              )

object UserProfileSqlModel {
  implicit val userProfileSqlRetrievalEncoder: Encoder[UserProfileSqlModel] = deriveEncoder[UserProfileSqlModel]
  implicit val userProfileSqlRetrievalDecoder: Decoder[UserProfileSqlModel] = deriveDecoder[UserProfileSqlModel]
}
