package repositories.user_profile

import cats.Monad
import cats.effect.Concurrent
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.*
import models.users.adts.Role
import models.wanderer.wanderer_profile.profile.UserLoginDetails
import cats.syntax.all.*

import java.sql.Timestamp
import java.time.LocalDateTime


trait UserLoginDetailsRepositoryAlgebra[F[_]] {

  def createUserLoginDetails(user: UserLoginDetails): F[Int]

  def findByUserId(userId: String): F[Option[UserLoginDetails]]

  def findByUsername(username: String): F[Option[UserLoginDetails]]

  def findByEmail(email: String): F[Option[UserLoginDetails]]

  def updateUserLoginDetails(userId: String, userLoginDetails: UserLoginDetails): F[Option[UserLoginDetails]]

  def updateUserLoginDetailsDynamic(
                                     userId: String,
                                     username: Option[String],
                                     passwordHash: Option[String],
                                     email: Option[String],
                                     role: Option[Role]
                                   ): F[Option[UserLoginDetails]]
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
              ${user.userId},
              ${user.username},
              ${user.passwordHash},
              ${user.email},
              ${user.role.toString},
              ${user.createdAt},
              ${user.updatedAt}
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
            passwordHash = ${userLoginDetails.passwordHash},
            email = ${userLoginDetails.email},
            role = ${userLoginDetails.role}
        WHERE user_id = $userId
      """.update.run

    // Query the updated user
    val selectQuery: ConnectionIO[Option[UserLoginDetails]] =
      sql"""
        SELECT user_id, username, password_hash, email, role, created_at, updated_at
        FROM user_login_details
        WHERE user_id = $userId
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

  override def updateUserLoginDetailsDynamic(
                                              userId: String,
                                              username: Option[String],
                                              passwordHash: Option[String],
                                              email: Option[String],
                                              role: Option[Role]
                                            ): F[Option[UserLoginDetails]] = {

    // Dynamically build the update query
    val updates = List(
      username.map(u => fr"username = $u"),
      passwordHash.map(ph => fr"password_hash = $ph"),
      email.map(e => fr"email = $e"),
      role.map(r => fr"role = ${r.toString}")
    ).flatten

    // Only execute if there are fields to update
    val updateQuery: Option[ConnectionIO[Int]] =
      if (updates.nonEmpty) {
        (fr"UPDATE user_login_details SET" ++ updates.intercalate(fr",") ++
          fr"WHERE user_id = $userId").update.run.some
      } else None

    // Query the updated user
    val selectQuery: ConnectionIO[Option[UserLoginDetails]] =
      sql"""
        SELECT id, user_id, username, password_hash, email, role, created_at, updated_at
        FROM user_login_details
        WHERE user_id = $userId
      """.query[UserLoginDetails].option

    // Combine update and select logic
    val result: ConnectionIO[Option[UserLoginDetails]] = updateQuery match {
      case Some(query) =>
        for {
          rowsAffected <- query
          updatedUser <- if (rowsAffected > 0) selectQuery else none[UserLoginDetails].pure[ConnectionIO]
        } yield updatedUser
      case None =>
        selectQuery // If no updates, just fetch the existing user
    }

    // Transact and return the result
    result.transact(transactor)
  }


}
