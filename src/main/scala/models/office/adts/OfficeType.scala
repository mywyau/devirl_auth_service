package models.office.adts

import io.circe.Decoder
import io.circe.Encoder

sealed trait OfficeType

case object OpenPlanOffice extends OfficeType

case object ExecutiveOffice extends OfficeType

case object PrivateOffice extends OfficeType

case object SharedOffice extends OfficeType

case object CoworkingSpace extends OfficeType

case object MeetingRoom extends OfficeType

object OfficeType {

  def fromString(str: String): OfficeType =
    str match {
      case "CoworkingSpace" => CoworkingSpace
      case "ExecutiveOffice" => ExecutiveOffice
      case "MeetingRoom" => MeetingRoom
      case "PrivateOffice" => PrivateOffice
      case "OpenPlanOffice" => OpenPlanOffice
      case "SharedOffice" => SharedOffice
      case _ => throw new Exception(s"Unknown office type: $str")
    }

  implicit val officeTypeEncoder: Encoder[OfficeType] =
    Encoder.encodeString.contramap {
      case CoworkingSpace => "CoworkingSpace"
      case ExecutiveOffice => "ExecutiveOffice"
      case MeetingRoom => "MeetingRoom"
      case PrivateOffice => "PrivateOffice"
      case OpenPlanOffice => "OpenPlanOffice"
      case SharedOffice => "SharedOffice"
    }

  implicit val officeTypeDecoder: Decoder[OfficeType] =
    Decoder.decodeString.emap {
      case "CoworkingSpace" => Right(CoworkingSpace)
      case "ExecutiveOffice" => Right(ExecutiveOffice)
      case "MeetingRoom" => Right(MeetingRoom)
      case "PrivateOffice" => Right(PrivateOffice)
      case "OpenPlanOffice" => Right(OpenPlanOffice)
      case "SharedOffice" => Right(SharedOffice)
      case other => Left(s"Invalid office type: $other")
    }
}
