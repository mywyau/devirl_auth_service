package models.users.login.adts

import io.circe.{Decoder, Encoder}

sealed trait LoginError {
  val code: String
  val message: String
}

sealed trait LoginUsernameError extends LoginError

case object UsernameNotFound extends LoginUsernameError:
  override val code: String = this.toString
  override val message: String = "Username was not found"

case object UsernameNotIncorrect extends LoginUsernameError:
  override val code: String = this.toString
  override val message: String = "Username was not found"


sealed trait LoginPasswordError extends LoginError

case object LoginPasswordIncorrect extends LoginPasswordError:
  override val code: String = this.toString
  override val message: String = "Password was incorrect"


object LoginError {

  def fromString(str: String): LoginError =
    str match {
      case "LoginPasswordIncorrect" => LoginPasswordIncorrect
      case "UsernameNotFound" => UsernameNotFound
      case _ => throw new Exception(s"Unknown LoginError: $str")
    }

  implicit val bookingStatusEncoder: Encoder[LoginError] =
    Encoder.encodeString.contramap {
      case LoginPasswordIncorrect => "LoginPasswordIncorrect"
      case UsernameNotFound => "UsernameNotFound"
      case _ => throw new Exception(s"Unknown LoginError")
    }

  implicit val bookingStatusDecoder: Decoder[LoginError] =
    Decoder.decodeString.emap {
      case "LoginPasswordIncorrect" => Right(LoginPasswordIncorrect)
      case "UsernameNotFound" => Right(UsernameNotFound)
      case other => Left(s"Invalid LoginError: $other")
    }
}