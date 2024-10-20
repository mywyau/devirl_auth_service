package models.users

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.LocalDateTime

sealed trait User

case class Business(
                     id: Option[Int],
                     business_id: String,
                     business_name: String,
                     contact_number: String,
                     contact_email: String,
                     created_at: LocalDateTime
                   ) extends User

object Business {
  implicit val businessEncoder: Encoder[Business] = deriveEncoder[Business]
  implicit val businessDecoder: Decoder[Business] = deriveDecoder[Business]
}


case class Renter(
                   id: Option[Int],
                   renter_id: String,
                   renter_name: String,
                   contact_number: String,
                   contact_email: String,
                   street: String,
                   city: String,
                   country: String,
                   postcode: String,
                   created_at: LocalDateTime
                 ) extends User

object Renter {
  implicit val renterEncoder: Encoder[Renter] = deriveEncoder[Renter]
  implicit val renterDecoder: Decoder[Renter] = deriveDecoder[Renter]
}


case class Admin(id: Option[Int]) extends User

object Admin {
  implicit val adminEncoder: Encoder[Admin] = deriveEncoder[Admin]
  implicit val adminDecoder: Decoder[Admin] = deriveDecoder[Admin]
}
