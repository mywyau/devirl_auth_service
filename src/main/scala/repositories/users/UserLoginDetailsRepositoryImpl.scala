package repositories.users

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.applicative.*
import cats.syntax.flatMap.*
import cats.syntax.functor.*
import cats.syntax.option.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.*
import models.users.adts.Role
import models.users.wanderer_profile.profile.UserLoginDetails

import java.sql.Timestamp
import java.time.LocalDateTime


trait UserLoginDetailsRepositoryAlgebra[F[_]] {

  def createUserLoginDetails(user: UserLoginDetails): F[Int]

  def findByUserId(userId: String): F[Option[UserLoginDetails]]

  def findByUsername(username: String): F[Option[UserLoginDetails]]

  def findByEmail(email: String): F[Option[UserLoginDetails]]

  def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): F[Option[UserLoginDetails]]
}

class UserLoginDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends UserLoginDetailsRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def createUserLoginDetails(user: UserLoginDetails): F[Int] = {
    sql"""
      INSERT INTO user_login_details (
        user_id,
        username,
        password_hash,
        email,
        role,
        created_at,
        updated_at
      )
      VALUES (
              ${user.user_id},
              ${user.username},
              ${user.password_hash},
              ${user.email},
              ${user.role.toString},
              ${user.created_at},
              ${user.updated_at}
      )""".update
      .run
      .transact(transactor)
  }

  override def findByUserId(userId: String): F[Option[UserLoginDetails]] = {
    val findQuery: F[Option[UserLoginDetails]] =
      sql"SELECT * FROM user_login_details WHERE user_id = $userId"
        .query[UserLoginDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def findByUsername(username: String): F[Option[UserLoginDetails]] = {
    val findQuery: F[Option[UserLoginDetails]] =
      sql"SELECT * FROM user_login_details WHERE username = $username"
        .query[UserLoginDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def findByEmail(email: String): F[Option[UserLoginDetails]] = {
    val findQuery: F[Option[UserLoginDetails]] =
      sql"SELECT * FROM user_login_details WHERE email = $email"
        .query[UserLoginDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): F[Option[UserLoginDetails]] = {

    // Update the user's login details
    val updateQuery: ConnectionIO[Int] =
      sql"""
        UPDATE user_login_details
        SET username = ${userLoginDetails.username},
            password_hash = ${userLoginDetails.password_hash},
            email = ${userLoginDetails.email},
            role = ${userLoginDetails.role}
        WHERE userId = $userId
      """.update.run

    // Query the updated user
    val selectQuery: ConnectionIO[Option[UserLoginDetails]] =
      sql"""
        SELECT userId, username, password_hash, email, role, created_at
        FROM user_login_details
        WHERE userId = $userId
      """.query[UserLoginDetails].option

    // Combine update and select logic
    val result: ConnectionIO[Option[UserLoginDetails]] =
      for {
        rowsAffected <- updateQuery
        updatedUser <- if (rowsAffected == 1) selectQuery else none[UserLoginDetails].pure[ConnectionIO]
      } yield updatedUser

    // Transact and handle errors in the F context
    result.transact(transactor).flatMap {
      case Some(user) => Concurrent[F].pure(Some(user))
      case None => Concurrent[F].pure(None)
    }
  }

}
