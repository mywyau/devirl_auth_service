package models.business.business_address.errors

import io.circe.{Decoder, Encoder}

sealed trait BusinessAddressErrors {
  val code: String
  val errorMessage: String
}

case object BusinessAddressNotFound extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessEmptyStringField extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessStreetLengthError extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object BusinessInvalidFormat extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessStreetMissing extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object BusinessCountyLengthError extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object BusinessPostcodeInvalidFormat extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object BusinessUserNotFound extends BusinessAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object BusinessAddressErrors {

  def fromString(str: String): BusinessAddressErrors =
    str match {
      case "BusinessAddressNotFound" => BusinessAddressNotFound
      case "BusinessStreetLengthError" => BusinessStreetLengthError
      case "BusinessStreetMissing" => BusinessStreetMissing
      case "BusinessCountyLengthError" => BusinessCountyLengthError
      case "BusinessPostcodeInvalidFormat" => BusinessPostcodeInvalidFormat
      case "BusinessUserNotFound" => BusinessUserNotFound
      case "BusinessEmptyStringField" => BusinessEmptyStringField
      case "BusinessInvalidFormat" => BusinessInvalidFormat
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[BusinessAddressErrors] =
    Encoder.encodeString.contramap {
      case BusinessAddressNotFound => "BusinessAddressNotFound"
      case BusinessStreetLengthError => "BusinessStreetLengthError"
      case BusinessStreetMissing => "BusinessStreetMissing"
      case BusinessCountyLengthError => "BusinessCountyLengthError"
      case BusinessPostcodeInvalidFormat => "BusinessPostcodeInvalidFormat"
      case BusinessUserNotFound => "BusinessUserNotFound"
      case BusinessEmptyStringField => "BusinessEmptyStringField"
      case BusinessInvalidFormat => "BusinessInvalidFormat"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[BusinessAddressErrors] =
    Decoder.decodeString.emap {
      case "BusinessAddressNotFound" => Right(BusinessAddressNotFound)
      case "BusinessStreetLengthError" => Right(BusinessStreetLengthError)
      case "BusinessStreetMissing" => Right(BusinessStreetMissing)
      case "BusinessCountyLengthError" => Right(BusinessCountyLengthError)
      case "BusinessPostcodeInvalidFormat" => Right(BusinessPostcodeInvalidFormat)
      case "BusinessUserNotFound" => Right(BusinessUserNotFound)
      case "BusinessEmptyStringField" => Right(BusinessEmptyStringField)
      case "BusinessInvalidFormat" => Right(BusinessInvalidFormat)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
