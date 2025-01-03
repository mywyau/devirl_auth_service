package models.business.desk_listing.errors

import io.circe.Decoder
import io.circe.Encoder

sealed trait DeskListingErrors {
  val code: String
  val errorMessage: String
}

case object DeskListingNotFound extends DeskListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessEmptyStringField extends DeskListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessInvalidFormat extends DeskListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessUserNotFound extends DeskListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"

case object DatabaseError extends DeskListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "DatabaseError"

object DeskListingErrors {

  def fromString(str: String): DeskListingErrors =
    str match {
      case "DeskListingNotFound" => DeskListingNotFound
      case "BusinessUserNotFound" => BusinessUserNotFound
      case "BusinessEmptyStringField" => BusinessEmptyStringField
      case "BusinessInvalidFormat" => BusinessInvalidFormat
      case "DatabaseError" => DatabaseError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[DeskListingErrors] =
    Encoder.encodeString.contramap {
      case DeskListingNotFound => "DeskListingNotFound"
      case BusinessUserNotFound => "BusinessUserNotFound"
      case BusinessEmptyStringField => "BusinessEmptyStringField"
      case BusinessInvalidFormat => "BusinessInvalidFormat"
      case DatabaseError => "DatabaseError"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[DeskListingErrors] =
    Decoder.decodeString.emap {
      case "DeskListingNotFound" => Right(DeskListingNotFound)
      case "BusinessUserNotFound" => Right(BusinessUserNotFound)
      case "BusinessEmptyStringField" => Right(BusinessEmptyStringField)
      case "BusinessInvalidFormat" => Right(BusinessInvalidFormat)
      case "DatabaseError" => Right(DatabaseError)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
