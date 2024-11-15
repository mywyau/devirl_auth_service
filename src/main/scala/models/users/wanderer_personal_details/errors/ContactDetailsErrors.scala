package models.users.wanderer_personal_details.errors

import io.circe.{Decoder, Encoder}

sealed trait ContactDetailsErrors {
  val code: String
  val errorMessage: String
}

case object ContactDetailsNotFound extends ContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Address was not found"

case object PhoneNumberNotFound extends ContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Street was not found"

case object EmailNotFound extends ContactDetailsErrors:
  override val code: String = this.toString
  override val errorMessage: String = "Country was not found"


object ContactDetailsErrors {

  def fromString(str: String): ContactDetailsErrors =
    str match {
      case "ContactDetailsNotFound" => ContactDetailsNotFound
      case "PhoneNumberNotFound" => PhoneNumberNotFound
      case "EmailNotFound" => EmailNotFound
      case _ => throw new Exception(s"Unknown ContactDetailsErrors: $str")
    }

  implicit val contactDetailsErrorsEncoder: Encoder[ContactDetailsErrors] =
    Encoder.encodeString.contramap {
      case ContactDetailsNotFound => "ContactDetailsNotFound"
      case PhoneNumberNotFound => "PhoneNumberNotFound"
      case EmailNotFound => "EmailNotFound"
    }

  implicit val contactDetailsErrorsDecoder: Decoder[ContactDetailsErrors] =
    Decoder.decodeString.emap {
      case "ContactDetailsNotFound" => Right(ContactDetailsNotFound)
      case "PhoneNumberNotFound" => Right(PhoneNumberNotFound)
      case "EmailNotFound" => Right(EmailNotFound)
      case other => Left(s"Invalid ContactDetailsErrors Error: $other")
    }
}
