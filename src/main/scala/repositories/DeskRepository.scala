//package repositories
//
//import cats.effect.Sync
//import doobie._
//import doobie.implicits._
//import doobie.util.meta.Meta
//import models._
//
//import java.sql.Timestamp
//import java.time.LocalDateTime
//
//// Import JDBC metadata instances
//import doobie.implicits.javasql._
//
//class DeskRepository[F[_] : Sync](transactor: Transactor[F]) {
//
//  // Meta instance to map between LocalDateTime and Timestamp
//  implicit val localDateTimeMeta: Meta[LocalDateTime] =
//    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)
//
//  def findBookingById(bookingId: String): F[Option[Bookings]] = {
//    sql"SELECT * FROM bookings WHERE id = $bookingId"
//      .query[Bookings]
//      .option
//      .transact(transactor)
//  }
//
//  def getAllBookings: F[List[Bookings]] = {
//    sql"SELECT * FROM bookings"
//      .query[Bookings]
//      .to[List]
//      .transact(transactor)
//  }
//
//  def setBookings(booking: Bookings): F[Int] = {
//
//    // Correct logging to show the actual values (without "Some" in the log)
//    //    println(s"VALUES (${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})")
//
//    // Pass deskId and roomId as Option values, so Doobie will handle them
//    sql"""
//      INSERT INTO bookings (user_id, workspace_id, booking_date, start_time, end_time, status, created_at)
//      VALUES (${booking.user_id}, ${booking.workspace_id}, ${booking.booking_date}, ${booking.start_time}, ${booking.end_time}, ${booking.status}, ${booking.created_at})
//    """.update
//      .run
//      .transact(transactor)
//  }
//
//
//  //  def getAllDesks: F[List[Desk]] = {
//  //    sql"SELECT * FROM desks"
//  //      .query[Desk]
//  //      .to[List]
//  //      .transact(transactor)
//  //  }
//  //
//  //  def getAvailableDesks: F[List[Desk]] = {
//  //    sql"SELECT * FROM desks WHERE status = 'available'"
//  //      .query[Desk]
//  //      .to[List]
//  //      .transact(transactor)
//  //  }
//
//  //  def findDeskById(deskId: String): F[Option[Desk]] = {
//  //    sql"SELECT * FROM desks WHERE id = $deskId"
//  //      .query[Desk]
//  //      .option
//  //      .transact(transactor)
//  //  }
//
//
//  //  def bookDesk(booking: Booking): F[Int] = {
//  //    val deskId: Option[String] = booking.deskId
//  //    val roomId: Option[String] = booking.roomId
//  //
//  //    // Correct logging to show the actual values (without "Some" in the log)
//  //    println(s"VALUES (${booking.id}, ${booking.userId}, ${deskId.getOrElse("NULL")}, ${roomId.getOrElse("NULL")}, ${booking.startTime}, ${booking.endTime})")
//  //
//  //    // Pass deskId and roomId as Option values, so Doobie will handle them
//  //    sql"""
//  //    INSERT INTO bookings (id, user_id, desk_id, room_id, start_time, end_time)
//  //    VALUES (${booking.id}, ${booking.userId}, $deskId, $roomId, ${booking.startTime}, ${booking.endTime})
//  //  """.update.run.transact(transactor)
//  //  }
//
//
//}
