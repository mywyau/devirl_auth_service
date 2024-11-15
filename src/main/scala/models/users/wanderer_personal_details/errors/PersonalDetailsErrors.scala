package models.users.wanderer_personal_details.errors

import io.circe.{Decoder, Encoder}

sealed trait PersonalDetailsErrors {
  val code: String
  val errorMessage: String
}

case object PersonalDetailsNotFound extends PersonalDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not found"

case object PhoneNumberNotFound extends PersonalDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Street was not found"

case object EmailNotFound extends PersonalDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Country was not found"


object PersonalDetailsErrors {

  def fromString(str: String): PersonalDetailsErrors =
    str match {
      case "PersonalDetailsNotFound" => PersonalDetailsNotFound
      case "PhoneNumberNotFound" => PhoneNumberNotFound
      case "EmailNotFound" => EmailNotFound
      case _ => throw new Exception(s"Unknown PersonalDetailsErrors: $str")
    }

  implicit val contactDetailsErrorsEncoder: Encoder[PersonalDetailsErrors] =
    Encoder.encodeString.contramap {
      case PersonalDetailsNotFound => "PersonalDetailsNotFound"
      case PhoneNumberNotFound => "PhoneNumberNotFound"
      case EmailNotFound => "EmailNotFound"
    }

  implicit val contactDetailsErrorsDecoder: Decoder[PersonalDetailsErrors] =
    Decoder.decodeString.emap {
      case "PersonalDetailsNotFound" => Right(PersonalDetailsNotFound)
      case "PhoneNumberNotFound" => Right(PhoneNumberNotFound)
      case "EmailNotFound" => Right(EmailNotFound)
      case other => Left(s"Invalid PersonalDetailsErrors Error: $other")
    }
}
