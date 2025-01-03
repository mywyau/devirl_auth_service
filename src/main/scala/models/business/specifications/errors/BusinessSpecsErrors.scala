package models.business.specifications.errors

import io.circe.Decoder
import io.circe.Encoder

sealed trait BusinessSpecificationsErrors {
  val code: String
  val errorMessage: String
}

case object BusinessSpecificationsNotFound extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "specifications was not found"

case object BusinessSpecificationsNotCreated extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "specifications was not found"

case object BusinessSpecificationsDatabaseError extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "BusinessSpecifications DatabaseError"

case object BusinessSpecificationsEmptyStringField extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object BusinessSpecificationsInvalidFormat extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object BusinessSpecificationsUserNotFound extends BusinessSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"

object BusinessSpecificationsErrors {

  def fromString(str: String): BusinessSpecificationsErrors =
    str match {
      case "BusinessSpecificationsNotFound" => BusinessSpecificationsNotFound
      case "BusinessSpecificationsUserNotFound" => BusinessSpecificationsUserNotFound
      case "BusinessSpecificationsEmptyStringField" => BusinessSpecificationsEmptyStringField
      case "BusinessSpecificationsInvalidFormat" => BusinessSpecificationsInvalidFormat
      case _ => throw new Exception(s"Unknown Error: $str")
    }

  implicit val businessSpecificationsErrorsEncoder: Encoder[BusinessSpecificationsErrors] =
    Encoder.encodeString.contramap {
      case BusinessSpecificationsNotFound => "BusinessSpecificationsNotFound"
      case BusinessSpecificationsUserNotFound => "BusinessSpecificationsUserNotFound"
      case BusinessSpecificationsEmptyStringField => "BusinessSpecificationsEmptyStringField"
      case BusinessSpecificationsInvalidFormat => "BusinessSpecificationsInvalidFormat"
    }

  implicit val businessSpecificationsErrorsDecoder: Decoder[BusinessSpecificationsErrors] =
    Decoder.decodeString.emap {
      case "BusinessSpecificationsNotFound" => Right(BusinessSpecificationsNotFound)
      case "BusinessSpecificationsUserNotFound" => Right(BusinessSpecificationsUserNotFound)
      case "BusinessSpecificationsEmptyStringField" => Right(BusinessSpecificationsEmptyStringField)
      case "BusinessSpecificationsInvalidFormat" => Right(BusinessSpecificationsInvalidFormat)
      case str => Left(s"Decoding BusinessSpecifications Error: $str")
    }
}
