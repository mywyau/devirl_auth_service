package models.business.business_specs.errors

import io.circe.{Decoder, Encoder}

sealed trait BusinessSpecsErrors {
  val code: String
  val errorMessage: String
}

case object BusinessSpecsNotFound extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object BusinessSpecsEmptyStringField extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessSpecsStreetLengthError extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object BusinessSpecsInvalidFormat extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessSpecsStreetMissing extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object BusinessSpecsCountyLengthError extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object BusinessSpecsPostcodeInvalidFormat extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object BusinessSpecsUserNotFound extends BusinessSpecsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object BusinessSpecsErrors {

  def fromString(str: String): BusinessSpecsErrors =
    str match {
      case "BusinessSpecsNotFound" => BusinessSpecsNotFound
      case "BusinessSpecsStreetLengthError" => BusinessSpecsStreetLengthError
      case "BusinessSpecsStreetMissing" => BusinessSpecsStreetMissing
      case "BusinessSpecsCountyLengthError" => BusinessSpecsCountyLengthError
      case "BusinessSpecsPostcodeInvalidFormat" => BusinessSpecsPostcodeInvalidFormat
      case "BusinessSpecsUserNotFound" => BusinessSpecsUserNotFound
      case "BusinessSpecsEmptyStringField" => BusinessSpecsEmptyStringField
      case "BusinessSpecsInvalidFormat" => BusinessSpecsInvalidFormat
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val businessSpecsErrorsEncoder: Encoder[BusinessSpecsErrors] =
    Encoder.encodeString.contramap {
      case BusinessSpecsNotFound => "BusinessSpecsNotFound"
      case BusinessSpecsStreetLengthError => "BusinessSpecsStreetLengthError"
      case BusinessSpecsStreetMissing => "BusinessSpecsStreetMissing"
      case BusinessSpecsCountyLengthError => "BusinessSpecsCountyLengthError"
      case BusinessSpecsPostcodeInvalidFormat => "BusinessSpecsPostcodeInvalidFormat"
      case BusinessSpecsUserNotFound => "BusinessSpecsUserNotFound"
      case BusinessSpecsEmptyStringField => "BusinessSpecsEmptyStringField"
      case BusinessSpecsInvalidFormat => "BusinessSpecsInvalidFormat"
    }

  implicit val businessSpecsErrorsDecoder: Decoder[BusinessSpecsErrors] =
    Decoder.decodeString.emap {
      case "BusinessSpecsNotFound" => Right(BusinessSpecsNotFound)
      case "BusinessSpecsStreetLengthError" => Right(BusinessSpecsStreetLengthError)
      case "BusinessSpecsStreetMissing" => Right(BusinessSpecsStreetMissing)
      case "BusinessSpecsCountyLengthError" => Right(BusinessSpecsCountyLengthError)
      case "BusinessSpecsPostcodeInvalidFormat" => Right(BusinessSpecsPostcodeInvalidFormat)
      case "BusinessSpecsUserNotFound" => Right(BusinessSpecsUserNotFound)
      case "BusinessSpecsEmptyStringField" => Right(BusinessSpecsEmptyStringField)
      case "BusinessSpecsInvalidFormat" => Right(BusinessSpecsInvalidFormat)
      case str => Left(s"Decoding BusinessSpecs Address Error: $str")
    }
}
