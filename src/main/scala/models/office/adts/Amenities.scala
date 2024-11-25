package models.office.adts

import io.circe.{Decoder, Encoder}

sealed trait Amenities

case object Wifi extends Amenities
case object Coffee extends Amenities
case object Kitchen extends Amenities
case object Printing extends Amenities

object Amenities {

  def fromString(str: String): Amenities =
    str match {
      case "Wifi" => Wifi
      case "Coffee" => Coffee
      case "Kitchen" => Kitchen
      case "Printing" => Printing
      case _ => throw new Exception(s"Unknown amenities type: $str")
    }

  implicit val amenitiesEncoder: Encoder[Amenities] =
    Encoder.encodeString.contramap {
      case Wifi => "Wifi"
      case Coffee => "Coffee"
      case Kitchen => "Kitchen"
      case Printing => "Printing"
    }

  implicit val amenitiesDecoder: Decoder[Amenities] =
    Decoder.decodeString.emap {
      case "Wifi" => Right(Wifi)
      case "Coffee" => Right(Coffee)
      case "Kitchen" => Right(Kitchen)
      case "Printing" => Right(Printing)
      case other => Left(s"Invalid amenities type: $other")
    }
}