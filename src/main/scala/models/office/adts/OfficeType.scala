package models.office.adts

import io.circe.{Decoder, Encoder}

sealed trait OfficeType

case object OpenPlanOffice extends OfficeType

case object ExecutiveOffice extends OfficeType

case object PrivateOffice extends OfficeType

object OfficeType {

  def fromString(str: String): OfficeType =
    str match {
      case "OpenPlanOffice" => OpenPlanOffice
      case "ExecutiveOffice" => ExecutiveOffice
      case "PrivateOffice" => PrivateOffice
      case _ => throw new Exception(s"Unknown desk type: $str")
    }

  implicit val officeTypeEncoder: Encoder[OfficeType] =
    Encoder.encodeString.contramap {
      case OpenPlanOffice => "OpenPlanOffice"
      case ExecutiveOffice => "ExecutiveOffice"
      case PrivateOffice => "PrivateOffice"
    }

  implicit val officeTypeDecoder: Decoder[OfficeType] =
    Decoder.decodeString.emap {
      case "OpenPlanOffice" => Right(OpenPlanOffice)
      case "ExecutiveOffice" => Right(ExecutiveOffice)
      case "PrivateOffice" => Right(PrivateOffice)
      case other => Left(s"Invalid desk type: $other")
    }
}