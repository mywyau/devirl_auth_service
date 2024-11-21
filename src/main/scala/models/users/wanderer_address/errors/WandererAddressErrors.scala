package models.users.wanderer_address.errors

import io.circe.{Decoder, Encoder}

sealed trait WandererAddressErrors {
  val code: String
  val errorMessage: String
}

case object AddressNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "address was not found"

case object EmptyStringField extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "empty field is invalid"

case object StreetLengthError extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name must be between 3 and 200 characters"

case object InvalidFormat extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "invalid format for field input it may contain special characters or breaks formatting validation"

case object StreetMissing extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "street name is missing"

case object CountyLengthError extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "county must be between 2 and 200 characters"

case object PostcodeInvalidFormat extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "postcode format is invalid"

case object UserNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "user was not found"


object WandererAddressErrors {

  def fromString(str: String): WandererAddressErrors =
    str match {
      case "AddressNotFound" => AddressNotFound
      case "StreetLengthError" => StreetLengthError
      case "StreetMissing" => StreetMissing
      case "CountyLengthError" => CountyLengthError
      case "PostcodeInvalidFormat" => PostcodeInvalidFormat
      case "UserNotFound" => UserNotFound
      case "EmptyStringField" => EmptyStringField
      case "InvalidFormat" => InvalidFormat
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[WandererAddressErrors] =
    Encoder.encodeString.contramap {
      case AddressNotFound => "AddressNotFound"
      case StreetLengthError => "StreetLengthError"
      case StreetMissing => "StreetMissing"
      case CountyLengthError => "CountyLengthError"
      case PostcodeInvalidFormat => "PostcodeInvalidFormat"
      case UserNotFound => "UserNotFound"
      case EmptyStringField => "EmptyStringField"
      case InvalidFormat => "InvalidFormat"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[WandererAddressErrors] =
    Decoder.decodeString.emap {
      case "AddressNotFound" => Right(AddressNotFound)
      case "StreetLengthError" => Right(StreetLengthError)
      case "StreetMissing" => Right(StreetMissing)
      case "CountyLengthError" => Right(CountyLengthError)
      case "PostcodeInvalidFormat" => Right(PostcodeInvalidFormat)
      case "UserNotFound" => Right(UserNotFound)
      case "EmptyStringField" => Right(EmptyStringField)
      case "InvalidFormat" => Right(InvalidFormat)
      case str => Left(s"Decoding Wanderer Address Error: $str")
    }
}
