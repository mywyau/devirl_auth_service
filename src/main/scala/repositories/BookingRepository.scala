package repositories

import cats.effect.Sync
import doobie._
import doobie.implicits._
import doobie.util.meta.Meta
import models._
import java.time.LocalDateTime
import java.sql.Timestamp

class BookingRepository[F[_]: Sync](transactor: Transactor[F]) {

  // Define Meta[Timestamp] explicitly if it's not available
  implicit val timestampMeta: Meta[Timestamp] = Meta[Long].imap(new Timestamp(_))(_.getTime)

  // Teach Doobie how to map LocalDateTime to SQL timestamps
  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].imap(
    timestamp => timestamp.toLocalDateTime     // Convert Timestamp to LocalDateTime when reading
  )(                                           // Convert LocalDateTime to Timestamp when writing
    localDateTime => Timestamp.valueOf(localDateTime)
  )

  def getAvailableDesks: F[List[Desk]] = {
    sql"SELECT * FROM desks WHERE status = 'available'"
      .query[Desk]
      .to[List]
      .transact(transactor)
  }

  def findDeskById(deskId: String): F[Option[Desk]] = {
    sql"SELECT * FROM desks WHERE id = $deskId"
      .query[Desk]
      .option
      .transact(transactor)
  }

  def bookDesk(booking: Booking): F[Int] = {
    sql"""
      INSERT INTO bookings (id, user_id, desk_id, start_time, end_time)
      VALUES (${booking.id}, ${booking.userId}, ${booking.deskId}, ${booking.startTime}, ${booking.endTime})
    """.update.run.transact(transactor)
  }
}
