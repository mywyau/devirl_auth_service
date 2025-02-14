package models

import io.circe.Decoder
import io.circe.Encoder

sealed trait Day

case object Monday extends Day
case object Tuesday extends Day
case object Wednesday extends Day
case object Thursday extends Day
case object Friday extends Day
case object Saturday extends Day
case object Sunday extends Day

object Day {

  def fromString(str: String): Day =
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

  implicit val dayEncoder: Encoder[Day] =
    Encoder.encodeString.contramap {
      case Monday => "Monday"
      case Tuesday => "Tuesday"
      case Wednesday => "Wednesday"
      case Thursday => "Thursday"
      case Friday => "Friday"
      case Saturday => "Saturday"
      case Sunday => "Sunday"
    }

  implicit val dayDecoder: Decoder[Day] =
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
