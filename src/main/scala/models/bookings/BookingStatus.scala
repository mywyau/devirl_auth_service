package models.bookings

import io.circe.{Decoder, Encoder}


sealed trait BookingStatus

case object Pending extends BookingStatus

case object Confirmed extends BookingStatus

case object Cancelled extends BookingStatus

// Custom Encoder/Decoder for BookingStatus to treat it as a string in JSON
object BookingStatus {

  def fromString(str: String): BookingStatus =
    str match {
      case "Confirmed" => Confirmed
      case "Pending" => Pending
      case "Cancelled" => Cancelled
      case _ => throw new Exception(s"Unknown status: $str")
    }

  implicit val bookingStatusEncoder: Encoder[BookingStatus] =
    Encoder.encodeString.contramap {
      case Confirmed => "Confirmed"
      case Pending => "Pending"
      case Cancelled => "Cancelled"
    }

  implicit val bookingStatusDecoder: Decoder[BookingStatus] =
    Decoder.decodeString.emap {
      case "Confirmed" => Right(Confirmed)
      case "Pending" => Right(Pending)
      case "Cancelled" => Right(Cancelled)
      case other => Left(s"Invalid booking status: $other")
    }
}
