package models.desk.deskSpecifications

import io.circe.Decoder
import io.circe.Encoder

sealed trait DeskType

case object StandingDesk extends DeskType
case object PrivateDesk extends DeskType
case object SharedDesk extends DeskType
case object HotDesk extends DeskType

object DeskType {

  def fromString(str: String): DeskType =
    str match {
      case "StandingDesk" => StandingDesk
      case "PrivateDesk" => PrivateDesk
      case "SharedDesk" => SharedDesk
      case "HotDesk" => HotDesk
      case _ => throw new Exception(s"Unknown desk type: $str")
    }

  implicit val bookingStatusEncoder: Encoder[DeskType] =
    Encoder.encodeString.contramap {
      case StandingDesk => "StandingDesk"
      case PrivateDesk => "PrivateDesk"
      case SharedDesk => "SharedDesk"
      case HotDesk => "HotDesk"
    }

  implicit val bookingStatusDecoder: Decoder[DeskType] =
    Decoder.decodeString.emap {
      case "StandingDesk" => Right(StandingDesk)
      case "PrivateDesk" => Right(PrivateDesk)
      case "SharedDesk" => Right(SharedDesk)
      case "HotDesk" => Right(HotDesk)
      case other => Left(s"Invalid desk type: $other")
    }
}
