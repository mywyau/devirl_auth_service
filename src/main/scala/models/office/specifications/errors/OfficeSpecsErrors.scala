package models.office.specifications.errors

import io.circe.{Decoder, Encoder}

sealed trait OfficeSpecificationsErrors {
  val code: String
  val errorMessage: String
}

case object OfficeSpecificationsNotFound extends OfficeSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "office specifications was not found"

case object OfficeSpecificationsNotCreated extends OfficeSpecificationsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "office specifications was not created"

case object OfficeSpecificationsDatabaseError extends OfficeSpecificationsErrors:
    override val code: String = this.toString
    override val errorMessage: String = "office specifications database error"


object OfficeSpecificationsErrors {

  def fromString(str: String): OfficeSpecificationsErrors =
    str match {
      case "OfficeSpecificationsNotFound" => OfficeSpecificationsNotFound
      case "OfficeSpecificationsNotCreated" => OfficeSpecificationsNotCreated
      case "OfficeSpecificationsDatabaseError" => OfficeSpecificationsDatabaseError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[OfficeSpecificationsErrors] =
    Encoder.encodeString.contramap {
      case OfficeSpecificationsNotFound => "OfficeSpecificationsNotFound"
      case OfficeSpecificationsNotCreated => "OfficeSpecificationsNotCreated"
      case OfficeSpecificationsDatabaseError => "OfficeSpecificationsDatabaseError"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[OfficeSpecificationsErrors] =
    Decoder.decodeString.emap {
      case "OfficeSpecificationsNotFound" => Right(OfficeSpecificationsNotFound)
      case "OfficeSpecificationsNotCreated" => Right(OfficeSpecificationsNotCreated)
      case "OfficeSpecificationsDatabaseError" => Right(OfficeSpecificationsDatabaseError)
      case str => Left(s"Decoding Office Specifications Error: $str")
    }
}
