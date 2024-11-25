package models.office.office_listing.errors

import io.circe.{Decoder, Encoder}

sealed trait OfficeListingErrors {
  val code: String
  val errorMessage: String
}

case object OfficeListingNotFound extends OfficeListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object OfficeEmptyStringField extends OfficeListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object OfficeInvalidFormat extends OfficeListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object OfficeUserNotFound extends OfficeListingErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"

case object OfficeDatabaseError extends OfficeListingErrors:
    override val code: String = this.toString
    override val errorMessage: String = "OfficeDatabaseError"


object OfficeListingErrors {

  def fromString(str: String): OfficeListingErrors =
    str match {
      case "OfficeListingNotFound" => OfficeListingNotFound
      case "OfficeEmptyStringField" => OfficeEmptyStringField
      case "OfficeInvalidFormat" => OfficeInvalidFormat
      case "OfficeUserNotFound" => OfficeUserNotFound
      case "OfficeDatabaseError" => OfficeDatabaseError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val officeListingErrorsEncoder: Encoder[OfficeListingErrors] =
    Encoder.encodeString.contramap {
      case OfficeListingNotFound => "OfficeListingNotFound"
      case OfficeEmptyStringField => "OfficeEmptyStringField"
      case OfficeInvalidFormat => "OfficeInvalidFormat"
      case OfficeUserNotFound => "OfficeUserNotFound"
      case OfficeDatabaseError => "OfficeDatabaseError"
    }

  implicit val officeListingErrorsDecoder: Decoder[OfficeListingErrors] =
    Decoder.decodeString.emap {
      case "OfficeListingNotFound" => Right(OfficeListingNotFound)
      case "OfficeEmptyStringField" => Right(OfficeEmptyStringField)
      case "OfficeInvalidFormat" => Right(OfficeInvalidFormat)
      case "OfficeUserNotFound" => Right(OfficeUserNotFound)
      case "OfficeDatabaseError" => Right(OfficeDatabaseError)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
