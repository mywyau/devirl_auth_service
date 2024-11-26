package models.office.office_address.errors

import io.circe.{Decoder, Encoder}

sealed trait OfficeAddressErrors {
  val code: String
  val errorMessage: String
}

case object OfficeAddressNotFound extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object OfficeEmptyStringField extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object OfficeStreetLengthError extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object OfficeInvalidFormat extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object OfficeStreetMissing extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object OfficeCountyLengthError extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object OfficePostcodeInvalidFormat extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object OfficeUserNotFound extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object OfficeAddressErrors {

  def fromString(str: String): OfficeAddressErrors =
    str match {
      case "OfficeAddressNotFound" => OfficeAddressNotFound
      case "OfficeStreetLengthError" => OfficeStreetLengthError
      case "OfficeStreetMissing" => OfficeStreetMissing
      case "OfficeCountyLengthError" => OfficeCountyLengthError
      case "OfficePostcodeInvalidFormat" => OfficePostcodeInvalidFormat
      case "OfficeUserNotFound" => OfficeUserNotFound
      case "OfficeEmptyStringField" => OfficeEmptyStringField
      case "OfficeInvalidFormat" => OfficeInvalidFormat
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[OfficeAddressErrors] =
    Encoder.encodeString.contramap {
      case OfficeAddressNotFound => "OfficeAddressNotFound"
      case OfficeStreetLengthError => "OfficeStreetLengthError"
      case OfficeStreetMissing => "OfficeStreetMissing"
      case OfficeCountyLengthError => "OfficeCountyLengthError"
      case OfficePostcodeInvalidFormat => "OfficePostcodeInvalidFormat"
      case OfficeUserNotFound => "OfficeUserNotFound"
      case OfficeEmptyStringField => "OfficeEmptyStringField"
      case OfficeInvalidFormat => "OfficeInvalidFormat"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[OfficeAddressErrors] =
    Decoder.decodeString.emap {
      case "OfficeAddressNotFound" => Right(OfficeAddressNotFound)
      case "OfficeStreetLengthError" => Right(OfficeStreetLengthError)
      case "OfficeStreetMissing" => Right(OfficeStreetMissing)
      case "OfficeCountyLengthError" => Right(OfficeCountyLengthError)
      case "OfficePostcodeInvalidFormat" => Right(OfficePostcodeInvalidFormat)
      case "OfficeUserNotFound" => Right(OfficeUserNotFound)
      case "OfficeEmptyStringField" => Right(OfficeEmptyStringField)
      case "OfficeInvalidFormat" => Right(OfficeInvalidFormat)
      case str => Left(s"Decoding Office Address Error: $str")
    }
}
