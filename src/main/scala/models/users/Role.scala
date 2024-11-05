package models.users

import io.circe.{Decoder, Encoder}

sealed trait Role

case object Admin extends Role
case object Business extends Role
case object Employee extends Role
case object Wanderer extends Role
case object Manager extends Role


// Custom Encoder/Decoder for BookingStatus to treat it as a string in JSON
object Role {

  def fromString(str: String): Role =
    str match {
      case "Admin" => Admin
      case "Business" => Business
      case "Employee" => Employee
      case "Wanderer" => Wanderer
      case "Manager" => Manager
      case _ => throw new Exception(s"Unknown role: $str")
    }

  implicit val bookingStatusEncoder: Encoder[Role] =
    Encoder.encodeString.contramap {
      case Admin => "Admin"
      case Business => "Business"
      case Employee => "Employee"
      case Wanderer => "Wanderer"
      case Manager => "Manager"
    }

  implicit val bookingStatusDecoder: Decoder[Role] =
    Decoder.decodeString.emap {
      case "Admin" => Right(Admin)
      case "Business" => Right(Business)
      case "Employee" => Right(Employee)
      case "Wanderer" => Right(Wanderer)
      case "Manager" => Right(Manager)
      case other => Left(s"Invalid role: $other")
    }
}

sealed trait Permission

case object CreateBooking extends Permission

case object ViewBooking extends Permission

case object EditBooking extends Permission

case object DeleteBooking extends Permission

object Permissions {

  val rolePermissions: Map[Role, Set[Permission]] =
    Map(
      Admin -> Set(CreateBooking, ViewBooking, EditBooking, DeleteBooking),
      Manager -> Set(ViewBooking, EditBooking),
      Employee -> Set(ViewBooking)
    )
}