package models.business.business_listing.errors

import io.circe.Decoder
import io.circe.Encoder

sealed trait BusinessListingErrors {
  val code: String
  val errorMessage: String
}

case object BusinessListingNotFound extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object MultipleErrors extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "MultipleErrors"

case object DatabaseError extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "MultipleErrors"

case object BusinessEmptyStringField extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessInvalidFormat extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessUserNotFound extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"

case object BusinessDatabaseError extends BusinessListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "BusinessDatabaseError"

object BusinessListingErrors {

  def fromString(str: String): BusinessListingErrors =
    str match {
      case "BusinessListingNotFound" => BusinessListingNotFound
      case "MultipleErrors" => MultipleErrors
      case "DatabaseError" => DatabaseError
      case "BusinessEmptyStringField" => BusinessEmptyStringField
      case "BusinessInvalidFormat" => BusinessInvalidFormat
      case "BusinessUserNotFound" => BusinessUserNotFound
      case "BusinessDatabaseError" => BusinessDatabaseError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val businessListingErrorsEncoder: Encoder[BusinessListingErrors] =
    Encoder.encodeString.contramap {
      case BusinessListingNotFound => "BusinessListingNotFound"
      case MultipleErrors => "MultipleErrors"
      case DatabaseError => "DatabaseError"
      case BusinessEmptyStringField => "BusinessEmptyStringField"
      case BusinessInvalidFormat => "BusinessInvalidFormat"
      case BusinessUserNotFound => "BusinessUserNotFound"
      case BusinessDatabaseError => "BusinessDatabaseError"
    }

  implicit val businessListingErrorsDecoder: Decoder[BusinessListingErrors] =
    Decoder.decodeString.emap {
      case "BusinessListingNotFound" => Right(BusinessListingNotFound)
      case "MultipleErrors" => Right(MultipleErrors)
      case "DatabaseError" => Right(DatabaseError)
      case "BusinessEmptyStringField" => Right(BusinessEmptyStringField)
      case "BusinessInvalidFormat" => Right(BusinessInvalidFormat)
      case "BusinessUserNotFound" => Right(BusinessUserNotFound)
      case "BusinessDatabaseError" => Right(BusinessDatabaseError)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
