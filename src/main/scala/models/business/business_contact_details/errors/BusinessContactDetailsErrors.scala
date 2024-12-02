package models.business.business_contact_details.errors

import io.circe.{Decoder, Encoder}

sealed trait BusinessContactDetailsErrors {
  val code: String
  val errorMessage: String
}

case object BusinessContactDetailsNotCreated extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessContactDetailsDatabaseError extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "DatabaseError"

case object BusinessContactDetailsNotFound extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessEmptyStringField extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessStreetLengthError extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object BusinessInvalidFormat extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessStreetMissing extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object BusinessCountyLengthError extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object BusinessPostcodeInvalidFormat extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object BusinessUserNotFound extends BusinessContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object BusinessContactDetailsErrors {

  def fromString(str: String): BusinessContactDetailsErrors =
    str match {
      case "BusinessContactDetailsNotCreated" => BusinessContactDetailsNotCreated
      case "BusinessContactDetailsDatabaseError" => BusinessContactDetailsDatabaseError
      case "BusinessContactDetailsNotFound" => BusinessContactDetailsNotFound
      case "BusinessInvalidFormat" => BusinessInvalidFormat
      case "BusinessStreetLengthError" => BusinessStreetLengthError
      case "BusinessStreetMissing" => BusinessStreetMissing
      case "BusinessCountyLengthError" => BusinessCountyLengthError
      case "BusinessPostcodeInvalidFormat" => BusinessPostcodeInvalidFormat
      case "BusinessUserNotFound" => BusinessUserNotFound
      case "BusinessEmptyStringField" => BusinessEmptyStringField
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val businessContactDetailsErrorsEncoder: Encoder[BusinessContactDetailsErrors] =
    Encoder.encodeString.contramap {
      case BusinessContactDetailsNotCreated => "BusinessContactDetailsNotCreated"
      case BusinessContactDetailsDatabaseError => "BusinessContactDetailsDatabaseError"
      case BusinessContactDetailsNotFound => "BusinessContactDetailsNotFound"
      case BusinessInvalidFormat => "BusinessInvalidFormat"
      case BusinessStreetLengthError => "BusinessStreetLengthError"
      case BusinessStreetMissing => "BusinessStreetMissing"
      case BusinessCountyLengthError => "BusinessCountyLengthError"
      case BusinessPostcodeInvalidFormat => "BusinessPostcodeInvalidFormat"
      case BusinessUserNotFound => "BusinessUserNotFound"
      case BusinessEmptyStringField => "BusinessEmptyStringField"
    }

  implicit val businessContactDetailsErrorsDecoder: Decoder[BusinessContactDetailsErrors] =
    Decoder.decodeString.emap {
      case "BusinessContactDetailsNotCreated" => Right(BusinessContactDetailsNotCreated)
      case "BusinessContactDetailsDatabaseError" => Right(BusinessContactDetailsDatabaseError)
      case "BusinessContactDetailsNotFound" => Right(BusinessContactDetailsNotFound)
      case "BusinessInvalidFormat" => Right(BusinessInvalidFormat)
      case "BusinessStreetLengthError" => Right(BusinessStreetLengthError)
      case "BusinessStreetMissing" => Right(BusinessStreetMissing)
      case "BusinessCountyLengthError" => Right(BusinessCountyLengthError)
      case "BusinessPostcodeInvalidFormat" => Right(BusinessPostcodeInvalidFormat)
      case "BusinessUserNotFound" => Right(BusinessUserNotFound)
      case "BusinessEmptyStringField" => Right(BusinessEmptyStringField)
      case str => Left(s"Decoding Business Address Error: $str")
    }
}
