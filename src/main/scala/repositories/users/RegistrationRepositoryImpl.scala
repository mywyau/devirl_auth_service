package repositories.users

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.applicative.*
import cats.syntax.applicativeError.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.syntax.option.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.adts.Role
import models.users.wanderer_profile.profile.UserProfile

import java.sql.Timestamp
import java.time.LocalDateTime


trait RegistrationRepositoryAlgebra[F[_]] {

  def createUser(user: UserProfile): F[Int]
}

class RegistrationRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends RegistrationRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def createUser(user: UserProfile): F[Int] = {
    sql"""
      INSERT INTO users (userId, username, password_hash, role, created_at)
      VALUES (${user.userId}, ${user.userLoginDetails.username}, ${user.userLoginDetails.password_hash}, ${user.first_name}, ${user.last_name}, ${user.contact_number}, ${user.email}, ${user.role.toString}, ${user.created_at})
    """.update
      .run
      .transact(transactor)
  }

}
