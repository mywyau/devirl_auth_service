package models.office.office_specs.errors

import io.circe.{Decoder, Encoder}

sealed trait OfficeSpecsErrors {
  val code: String
  val errorMessage: String
}

case object OfficeSpecsNotFound extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object OfficeSpecsEmptyStringField extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object OfficeSpecsStreetLengthError extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object OfficeSpecsInvalidFormat extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object OfficeSpecsStreetMissing extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object OfficeSpecsCountyLengthError extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object OfficeSpecsPostcodeInvalidFormat extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object OfficeSpecsUserNotFound extends OfficeSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object OfficeSpecsErrors {

  def fromString(str: String): OfficeSpecsErrors =
    str match {
      case "OfficeSpecsNotFound" => OfficeSpecsNotFound
      case "OfficeSpecsStreetLengthError" => OfficeSpecsStreetLengthError
      case "OfficeSpecsStreetMissing" => OfficeSpecsStreetMissing
      case "OfficeSpecsCountyLengthError" => OfficeSpecsCountyLengthError
      case "OfficeSpecsPostcodeInvalidFormat" => OfficeSpecsPostcodeInvalidFormat
      case "OfficeSpecsUserNotFound" => OfficeSpecsUserNotFound
      case "OfficeSpecsEmptyStringField" => OfficeSpecsEmptyStringField
      case "OfficeSpecsInvalidFormat" => OfficeSpecsInvalidFormat
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[OfficeSpecsErrors] =
    Encoder.encodeString.contramap {
      case OfficeSpecsNotFound => "OfficeSpecsNotFound"
      case OfficeSpecsStreetLengthError => "OfficeSpecsStreetLengthError"
      case OfficeSpecsStreetMissing => "OfficeSpecsStreetMissing"
      case OfficeSpecsCountyLengthError => "OfficeSpecsCountyLengthError"
      case OfficeSpecsPostcodeInvalidFormat => "OfficeSpecsPostcodeInvalidFormat"
      case OfficeSpecsUserNotFound => "OfficeSpecsUserNotFound"
      case OfficeSpecsEmptyStringField => "OfficeSpecsEmptyStringField"
      case OfficeSpecsInvalidFormat => "OfficeSpecsInvalidFormat"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[OfficeSpecsErrors] =
    Decoder.decodeString.emap {
      case "OfficeSpecsNotFound" => Right(OfficeSpecsNotFound)
      case "OfficeSpecsStreetLengthError" => Right(OfficeSpecsStreetLengthError)
      case "OfficeSpecsStreetMissing" => Right(OfficeSpecsStreetMissing)
      case "OfficeSpecsCountyLengthError" => Right(OfficeSpecsCountyLengthError)
      case "OfficeSpecsPostcodeInvalidFormat" => Right(OfficeSpecsPostcodeInvalidFormat)
      case "OfficeSpecsUserNotFound" => Right(OfficeSpecsUserNotFound)
      case "OfficeSpecsEmptyStringField" => Right(OfficeSpecsEmptyStringField)
      case "OfficeSpecsInvalidFormat" => Right(OfficeSpecsInvalidFormat)
      case str => Left(s"Decoding OfficeSpecs Address Error: $str")
    }
}
