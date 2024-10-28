package repositories

import cats.effect.Concurrent
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.meta.Meta
import models.bookings.BookingStatus
import models.users.{Role, User}

import java.sql.Timestamp
import java.time.LocalDateTime


trait UserRepositoryAlgebra[F[_]] {

  def createUser(user: User): F[Int]

  def findByUsername(username: String): F[Option[User]]

  def findByContactNumber(contactNumber: String): F[Option[User]]

  def findByEmail(email: String): F[Option[User]]
}

class UserRepositoryImpl[F[_] : Concurrent](transactor: Transactor[F]) extends UserRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  def createUser(user: User): F[Int] = {
    sql"""
      INSERT INTO users (username, password_hash, first_name, last_name, contact_number, email, role, created_at)
      VALUES (${user.username}, ${user.password_hash}, ${user.first_name}, ${user.last_name}, ${user.contact_number}, ${user.email}, ${user.role.toString}, ${user.created_at})
    """.update
      .run
      .transact(transactor)
  }

  def findByUsername(username: String): F[Option[User]] = {
    sql"SELECT * FROM users WHERE username = $username"
      .query[User]
      .option
      .transact(transactor)
  }

  def findByContactNumber(contactNumber: String): F[Option[User]] = {
    sql"SELECT * FROM users WHERE contact_number = $contactNumber"
      .query[User]
      .option
      .transact(transactor)
  }

  def findByEmail(email: String): F[Option[User]] = {
    sql"SELECT * FROM users WHERE email = $email"
      .query[User]
      .option
      .transact(transactor)
  }

}
