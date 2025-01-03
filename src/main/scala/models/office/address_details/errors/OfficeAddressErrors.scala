package models.office.address_details.errors

import io.circe.Decoder
import io.circe.Encoder

sealed trait OfficeAddressErrors {
  val code: String
  val errorMessage: String
}




case object OfficeAddressNotCreated extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not created"

case object OfficeAddressNotUpdated extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not updated"  

case object OfficeAddressNotFound extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not found"

case object OfficeUserNotFound extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "User was not found"

case object UnexpectedError extends OfficeAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "UnexpectedError occurred"  

object OfficeAddressErrors {

  def fromString(str: String): OfficeAddressErrors =
    str match {
      case "OfficeAddressNotCreated" => OfficeAddressNotCreated
      case "OfficeAddressNotUpdated" => OfficeAddressNotUpdated
      case "OfficeAddressNotFound" => OfficeAddressNotFound
      case "OfficeUserNotFound" => OfficeUserNotFound
      case "UnexpectedError" => UnexpectedError
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val officeAddressErrorsEncoder: Encoder[OfficeAddressErrors] =
    Encoder.encodeString.contramap {
      case OfficeAddressNotCreated => "OfficeAddressNotCreated"
      case OfficeAddressNotUpdated => "OfficeAddressNotUpdated"
      case OfficeAddressNotFound => "OfficeAddressNotFound"
      case OfficeUserNotFound => "OfficeUserNotFound"
      case UnexpectedError => "UnexpectedError"
    }

  implicit val officeAddressErrorsDecoder: Decoder[OfficeAddressErrors] =
    Decoder.decodeString.emap {
      case "OfficeAddressNotCreated" => Right(OfficeAddressNotCreated)
      case "OfficeAddressNotUpdated" => Right(OfficeAddressNotUpdated)
      case "OfficeAddressNotFound" => Right(OfficeAddressNotFound)
      case "OfficeUserNotFound" => Right(OfficeUserNotFound)
      case "UnexpectedError" => Right(UnexpectedError)
      case str => Left(s"Decoding Office Address Error: $str")
    }
}
