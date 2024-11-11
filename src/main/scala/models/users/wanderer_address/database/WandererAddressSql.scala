package models.users.wanderer_address.database

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import models.users.adts.Role

import java.time.LocalDateTime


case class WandererAddressSql(
                                  id: Int,
                                  userId: String,
                                  street: String,
                                  city: String,
                                  country: String,
                                  county: Option[String],
                                  postcode: String,
                                  created_at: LocalDateTime,
                                  updated_at: LocalDateTime
                                )

object WandererAddressSql {
  implicit val userProfileSqlRetrievalEncoder: Encoder[WandererAddressSql] = deriveEncoder[WandererAddressSql]
  implicit val userProfileSqlRetrievalDecoder: Decoder[WandererAddressSql] = deriveDecoder[WandererAddressSql]
}
