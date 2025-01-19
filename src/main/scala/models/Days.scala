package models

import io.circe.Decoder
import io.circe.Encoder

sealed trait Days

case object Monday extends Days
case object Tuesday extends Days
case object Wednesday extends Days
case object Thursday extends Days
case object Friday extends Days
case object Saturday extends Days
case object Sunday extends Days

object Days {

  def fromString(str: String): Days =
    str match {
      case "Monday" => Monday
      case "Tuesday" => Tuesday
      case "Wednesday" => Wednesday
      case "Thursday" => Thursday
      case "Friday" => Friday
      case "Saturday" => Saturday
      case "Sunday" => Sunday
      case _ => throw new Exception(s"Unknown day type: $str")
    }

  implicit val dayEncoder: Encoder[Days] =
    Encoder.encodeString.contramap {
      case Monday => "Monday"
      case Tuesday => "Tuesday"
      case Wednesday => "Wednesday"
      case Thursday => "Thursday"
      case Friday => "Friday"
      case Saturday => "Saturday"
      case Sunday => "Sunday"
    }

  implicit val dayDecoder: Decoder[Days] =
    Decoder.decodeString.emap {
      case "Monday" => Right(Monday)
      case "Tuesday" => Right(Tuesday)
      case "Wednesday" => Right(Wednesday)
      case "Thursday" => Right(Thursday)
      case "Friday" => Right(Friday)
      case "Saturday" => Right(Saturday)
      case "Sunday" => Right(Sunday)
      case other => Left(s"Invalid Day: $other")
    }
}
