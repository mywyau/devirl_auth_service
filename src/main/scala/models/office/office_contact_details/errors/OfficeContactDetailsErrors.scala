package models.office.office_contact_details.errors

import io.circe.{Decoder, Encoder}

sealed trait OfficeContactDetailsErrors {
  val code: String
  val errorMessage: String
}

case object OfficeContactDetailsNotCreated extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object OfficeContactDetailsDatabaseError extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "DatabaseError"

case object OfficeContactDetailsNotFound extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object OfficeEmptyStringField extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object OfficeStreetLengthError extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object OfficeInvalidFormat extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object OfficeStreetMissing extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object OfficeCountyLengthError extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object OfficePostcodeInvalidFormat extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object OfficeUserNotFound extends OfficeContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object OfficeContactDetailsErrors {

  def fromString(str: String): OfficeContactDetailsErrors =
    str match {
      case "OfficeContactDetailsNotCreated" => OfficeContactDetailsNotCreated
      case "OfficeContactDetailsDatabaseError" => OfficeContactDetailsDatabaseError
      case "OfficeContactDetailsNotFound" => OfficeContactDetailsNotFound
      case "OfficeInvalidFormat" => OfficeInvalidFormat
      case "OfficeStreetLengthError" => OfficeStreetLengthError
      case "OfficeStreetMissing" => OfficeStreetMissing
      case "OfficeCountyLengthError" => OfficeCountyLengthError
      case "OfficePostcodeInvalidFormat" => OfficePostcodeInvalidFormat
      case "OfficeUserNotFound" => OfficeUserNotFound
      case "OfficeEmptyStringField" => OfficeEmptyStringField
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[OfficeContactDetailsErrors] =
    Encoder.encodeString.contramap {
      case OfficeContactDetailsNotCreated => "OfficeContactDetailsNotCreated"
      case OfficeContactDetailsDatabaseError => "OfficeContactDetailsDatabaseError"
      case OfficeContactDetailsNotFound => "OfficeContactDetailsNotFound"
      case OfficeInvalidFormat => "OfficeInvalidFormat"
      case OfficeStreetLengthError => "OfficeStreetLengthError"
      case OfficeStreetMissing => "OfficeStreetMissing"
      case OfficeCountyLengthError => "OfficeCountyLengthError"
      case OfficePostcodeInvalidFormat => "OfficePostcodeInvalidFormat"
      case OfficeUserNotFound => "OfficeUserNotFound"
      case OfficeEmptyStringField => "OfficeEmptyStringField"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[OfficeContactDetailsErrors] =
    Decoder.decodeString.emap {
      case "OfficeContactDetailsNotCreated" => Right(OfficeContactDetailsNotCreated)
      case "OfficeContactDetailsDatabaseError" => Right(OfficeContactDetailsDatabaseError)
      case "OfficeContactDetailsNotFound" => Right(OfficeContactDetailsNotFound)
      case "OfficeInvalidFormat" => Right(OfficeInvalidFormat)
      case "OfficeStreetLengthError" => Right(OfficeStreetLengthError)
      case "OfficeStreetMissing" => Right(OfficeStreetMissing)
      case "OfficeCountyLengthError" => Right(OfficeCountyLengthError)
      case "OfficePostcodeInvalidFormat" => Right(OfficePostcodeInvalidFormat)
      case "OfficeUserNotFound" => Right(OfficeUserNotFound)
      case "OfficeEmptyStringField" => Right(OfficeEmptyStringField)
      case str => Left(s"Decoding Office Address Error: $str")
    }
}
