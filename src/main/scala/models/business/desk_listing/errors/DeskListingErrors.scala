package models.business.desk_listing.errors

import io.circe.{Decoder, Encoder}

sealed trait BusinessDeskErrors {
  val code: String
  val errorMessage: String
}

case object BusinessDeskNotFound extends BusinessDeskErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessEmptyStringField extends BusinessDeskErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessInvalidFormat extends BusinessDeskErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessUserNotFound extends BusinessDeskErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"

case object DatabaseError extends BusinessDeskErrors:
    override val code: String = this.toString
    override val errorMessage: String = "DatabaseError"


object BusinessDeskErrors {

  def fromString(str: String): BusinessDeskErrors =
    str match {
      case "BusinessDeskNotFound" => BusinessDeskNotFound
      case "BusinessUserNotFound" => BusinessUserNotFound
      case "BusinessEmptyStringField" => BusinessEmptyStringField
      case "BusinessInvalidFormat" => BusinessInvalidFormat
      case "DatabaseError" => DatabaseError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[BusinessDeskErrors] =
    Encoder.encodeString.contramap {
      case BusinessDeskNotFound => "BusinessDeskNotFound"
      case BusinessUserNotFound => "BusinessUserNotFound"
      case BusinessEmptyStringField => "BusinessEmptyStringField"
      case BusinessInvalidFormat => "BusinessInvalidFormat"
      case DatabaseError => "DatabaseError"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[BusinessDeskErrors] =
    Decoder.decodeString.emap {
      case "BusinessDeskNotFound" => Right(BusinessDeskNotFound)
      case "BusinessUserNotFound" => Right(BusinessUserNotFound)
      case "BusinessEmptyStringField" => Right(BusinessEmptyStringField)
      case "BusinessInvalidFormat" => Right(BusinessInvalidFormat)
      case "DatabaseError" => Right(DatabaseError)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
