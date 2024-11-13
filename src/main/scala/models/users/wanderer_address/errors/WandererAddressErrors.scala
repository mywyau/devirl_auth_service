package models.users.wanderer_address.errors

import io.circe.{Decoder, Encoder}

sealed trait WandererAddressErrors {
  val code: String
  val errorMessage: String
}

case object AddressNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not found"

case object StreetNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Street was not found"

case object CountryNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Country was not found"

case object PostCodeInvalid extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Postcode was is invalid"

case object UserNotFound extends WandererAddressErrors:
  override val code: String = this.toString
  override val errorMessage: String = "User was not found"


object WandererAddressErrors {

  def fromString(str: String): WandererAddressErrors =
    str match {
      case "AddressNotFound" => AddressNotFound
      case "StreetNotFound" => StreetNotFound
      case "CountryNotFound" => CountryNotFound
      case "PostCodeInvalid" => PostCodeInvalid
      case "UserNotFound" => UserNotFound
      case _ => throw new Exception(s"Unknown Address Error: $str")
    }

  implicit val wandererAddressErrorsEncoder: Encoder[WandererAddressErrors] =
    Encoder.encodeString.contramap {
      case AddressNotFound => "AddressNotFound"
      case StreetNotFound => "StreetNotFound"
      case CountryNotFound => "CountryNotFound"
      case PostCodeInvalid => "PostCodeInvalid"
      case UserNotFound => "UserNotFound"
    }

  implicit val wandererAddressErrorsDecoder: Decoder[WandererAddressErrors] =
    Decoder.decodeString.emap {
      case "AddressNotFound" => Right(AddressNotFound)
      case "StreetNotFound" => Right(StreetNotFound)
      case "CountryNotFound" => Right(CountryNotFound)
      case "PostCodeInvalid" => Right(PostCodeInvalid)
      case "UserNotFound" => Right(UserNotFound)
      case other => Left(s"Invalid Wanderer Address Error: $other")
    }
}
