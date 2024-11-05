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

import java.sql.Timestamp
import java.time.LocalDateTime


trait UserProfileRepositoryAlgebra[F[_]] {

  def createUserProfile(user: UserProfile): F[Int]

  def findByUserId(userId: String): F[Option[UserProfile]]

  def findByUsername(username: String): F[Option[UserProfile]]

  def findByContactNumber(contactNumber: String): F[Option[UserProfile]]

  def findByEmail(email: String): F[Option[UserProfile]]

  def updateUserRole(userId: String, desiredRole: Role): F[Option[UserProfile]]
}

class UserProfileRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends UserProfileRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def createUserProfile(user: UserProfile): F[Int] = {
//    println(
//      s"""
//    INSERT INTO user_profile (
//      userId,
//      username,
//      password_hash,
//      first_name,
//      last_name,
//      street,
//      city,
//      country,
//      county,
//      postcode,
//      contact_number,
//      email,
//      role,
//      created_at
//    ) VALUES (
//      '${user.userId}',
//      '${user.userLoginDetails.username}',
//      '${user.userLoginDetails.password_hash}',
//      '${user.first_name}',
//      '${user.last_name}',
//      '${user.userAddress.street}',
//      '${user.userAddress.city}',
//      '${user.userAddress.country}',
//      '${user.userAddress.county}',
//      '${user.userAddress.postcode}',
//      '${user.contact_number}',
//      '${user.email}',
//      '${user.role.toString}',
//      '${user.created_at}'
//    )
//  """
//    )

    sql"""
      INSERT INTO user_profile (
        userId,
        username,
        password_hash,
        first_name,
        last_name,
        street,
        city,
        country,
        county,
        postcode,
        contact_number,
        email,
        role,
        created_at
      )
      VALUES (
              ${user.userId},
              ${user.userLoginDetails.username},
              ${user.userLoginDetails.password_hash},
              ${user.first_name},
              ${user.last_name},
              ${user.userAddress.street},
              ${user.userAddress.city},
              ${user.userAddress.country},
              ${user.userAddress.county},
              ${user.userAddress.postcode},
              ${user.contact_number},
              ${user.email},
              ${user.role.toString},
              ${user.created_at}
      )""".update
      .run
      .transact(transactor)
  }

  override def findByUserId(userId: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlRetrieval]] =
      sql"SELECT * FROM user_profile WHERE userId = $userId"
        .query[UserProfileSqlRetrieval]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              userId = user.userId,
              username = user.username,
              password_hash = user.password_hash
            ),
          first_name = user.first_name,
          last_name = user.last_name,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = user.street,
              city = user.city,
              country = user.country,
              county = user.county,
              postcode = user.postcode,
              created_at = user.created_at
            ),
          contact_number = user.contact_number,
          email = user.email,
          role = user.role,
          created_at = user.created_at
        ))
      case None =>
        None
    }
  }

  override def findByUsername(username: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlRetrieval]] =
      sql"SELECT * FROM user_profile WHERE username = $username"
        .query[UserProfileSqlRetrieval]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              userId = user.userId,
              username = user.username,
              password_hash = user.password_hash
            ),
          first_name = user.first_name,
          last_name = user.last_name,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = user.street,
              city = user.city,
              country = user.country,
              county = user.county,
              postcode = user.postcode,
              created_at = user.created_at
            ),
          contact_number = user.contact_number,
          email = user.email,
          role = user.role,
          created_at = user.created_at
        ))
      case None =>
        None
    }
  }

  override def findByContactNumber(contactNumber: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlRetrieval]] =
      sql"SELECT * FROM user_profile WHERE contact_number = $contactNumber"
        .query[UserProfileSqlRetrieval]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              userId = user.userId,
              username = user.username,
              password_hash = user.password_hash
            ),
          first_name = user.first_name,
          last_name = user.last_name,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = user.street,
              city = user.city,
              country = user.country,
              county = user.county,
              postcode = user.postcode,
              created_at = user.created_at
            ),
          contact_number = user.contact_number,
          email = user.email,
          role = user.role,
          created_at = user.created_at
        ))
      case None =>
        None
    }
  }

  override def findByEmail(email: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlRetrieval]] =
      sql"SELECT * FROM user_profile WHERE email = $email"
        .query[UserProfileSqlRetrieval]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              userId = user.userId,
              username = user.username,
              password_hash = user.password_hash
            ),
          first_name = user.first_name,
          last_name = user.last_name,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = user.street,
              city = user.city,
              country = user.country,
              county = user.county,
              postcode = user.postcode,
              created_at = user.created_at
            ),
          contact_number = user.contact_number,
          email = user.email,
          role = user.role,
          created_at = user.created_at
        ))
      case None =>
        None
    }
  }


  override def updateUserRole(userId: String, desiredRole: Role): F[Option[UserProfile]] = {

    // Update the user's role
    val updateQuery: ConnectionIO[Int] =
      sql"""
        UPDATE user_profile SET role = $desiredRole WHERE userId = $userId
      """.update.run

    // Query the updated user
    val selectQuery: ConnectionIO[Option[UserProfileSqlRetrieval]] =
      sql"""
        SELECT userId, username, password_hash, first_name, last_name, street, city, country, county, postcode, contact_number, email, role, created_at
        FROM user_profile WHERE userId = $userId
      """.query[UserProfileSqlRetrieval].option

    // Combine update and select logic
    val result: ConnectionIO[Option[UserProfile]] =
      for {
        rowsAffected <- updateQuery
        updatedUser <- if (rowsAffected == 1) selectQuery else none[UserProfileSqlRetrieval].pure[ConnectionIO]
      } yield updatedUser.map { userSql =>
        // Transform UserProfileSqlRetrieval to UserProfile
        UserProfile(
          userId = userSql.userId,
          userLoginDetails =
            UserLoginDetails(
              userId = userSql.userId,
              username = userSql.username,
              password_hash = userSql.password_hash
            ),
          first_name = userSql.first_name,
          last_name = userSql.last_name,
          userAddress =
            UserAddress(
              userId = userSql.userId,
              street = userSql.street,
              city = userSql.city,
              country = userSql.country,
              county = userSql.county,
              postcode = userSql.postcode,
              created_at = userSql.created_at
            ),
          contact_number = userSql.contact_number,
          email = userSql.email,
          role = userSql.role,
          created_at = userSql.created_at
        )
      }

    // Transact and handle errors in the F context
    result.transact(transactor).flatMap {
      case Some(user) => Concurrent[F].pure(Some(user))
      case None => Concurrent[F].pure(None)
    }
  }


}
