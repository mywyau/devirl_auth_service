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
import models.users.wanderer_profile.database.UserProfileSqlModel
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserProfile}

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
    sql"""
      INSERT INTO user_profile (
        user_id,
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
              ${user.userLoginDetails.passwordHash},
              ${user.firstName},
              ${user.lastName},
              ${user.userAddress.street},
              ${user.userAddress.city},
              ${user.userAddress.country},
              ${user.userAddress.county},
              ${user.userAddress.postcode},
              ${user.contactNumber},
              ${user.email},
              ${user.role.toString},
              ${user.createdAt}
      )""".update
      .run
      .transact(transactor)
  }

  override def findByUserId(userId: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlModel]] =
      sql"SELECT * FROM user_login_details WHERE userId = $userId"
        .query[UserProfileSqlModel]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(
          UserProfile(
            userId = user.userId,
            userLoginDetails =
              UserLoginDetails(
                id = Some(user.id),
                userId = user.userId,
                username = user.username,
                passwordHash = user.passwordHash,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
              ),
            firstName = user.firstName,
            lastName = user.lastName,
            userAddress =
              UserAddress(
                userId = user.userId,
                street = Some(user.street),
                city = Some(user.city),
                country = Some(user.country),
                county = user.county,
                postcode = Some(user.postcode),
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
              ),
            contactNumber = user.contactNumber,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
          ))
      case None =>
        None
    }
  }

  override def findByUsername(username: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlModel]] =
      sql"SELECT * FROM user_profile WHERE username = $username"
        .query[UserProfileSqlModel]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(
          UserProfile(
            userId = user.userId,
            userLoginDetails =
              UserLoginDetails(
                id = Some(user.id),
                userId = user.userId,
                username = user.username,
                passwordHash = user.passwordHash,
                email = user.email,
                role = user.role,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
              ),
            firstName = user.firstName,
            lastName = user.lastName,
            userAddress =
              UserAddress(
                userId = user.userId,
                street = Some(user.street),
                city = Some(user.city),
                country = Some(user.country),
                county = user.county,
                postcode = Some(user.postcode),
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
              ),
            contactNumber = user.contactNumber,
            email = user.email,
            role = user.role,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
          ))
      case None =>
        None
    }
  }

  override def findByContactNumber(contactNumber: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlModel]] =
      sql"SELECT * FROM user_profile WHERE contact_number = $contactNumber"
        .query[UserProfileSqlModel]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              id = Some(user.id),
              userId = user.userId,
              username = user.username,
              passwordHash = user.passwordHash,
              email = user.email,
              role = user.role,
              createdAt = user.createdAt,
              updatedAt = user.updatedAt
            ),
          firstName = user.firstName,
          lastName = user.lastName,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = Some(user.street),
              city = Some(user.city),
              country = Some(user.country),
              county = user.county,
              postcode = Some(user.postcode),
              createdAt = user.createdAt,
              updatedAt = user.updatedAt
            ),
          contactNumber = user.contactNumber,
          email = user.email,
          role = user.role,
          createdAt = user.createdAt,
          updatedAt = user.updatedAt
        ))
      case None =>
        None
    }
  }

  override def findByEmail(email: String): F[Option[UserProfile]] = {
    // Define the query to retrieve a user profile by email
    val findQuery: F[Option[UserProfileSqlModel]] =
      sql"SELECT * FROM user_profile WHERE email = $email"
        .query[UserProfileSqlModel]
        .option
        .transact(transactor)

    // Process the query result
    findQuery.map {
      case Some(user) =>
        Some(UserProfile(
          userId = user.userId,
          userLoginDetails =
            UserLoginDetails(
              id = Some(user.id),
              userId = user.userId,
              username = user.username,
              passwordHash = user.passwordHash,
              email = user.email,
              role = user.role,
              createdAt = user.createdAt,
              updatedAt = user.updatedAt
            ),
          firstName = user.firstName,
          lastName = user.lastName,
          userAddress =
            UserAddress(
              userId = user.userId,
              street = Some(user.street),
              city = Some(user.city),
              country = Some(user.country),
              county = user.county,
              postcode = Some(user.postcode),
              createdAt = user.createdAt,
              updatedAt = user.updatedAt
            ),
          contactNumber = user.contactNumber,
          email = user.email,
          role = user.role,
          createdAt = user.createdAt,
          updatedAt = user.updatedAt
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
    val selectQuery: ConnectionIO[Option[UserProfileSqlModel]] =
      sql"""
        SELECT userId, username, password_hash, first_name, last_name, street, city, country, county, postcode, contact_number, email, role, created_at
        FROM user_profile WHERE userId = $userId
      """.query[UserProfileSqlModel].option

    // Combine update and select logic
    val result: ConnectionIO[Option[UserProfile]] =
      for {
        rowsAffected <- updateQuery
        updatedUser <- if (rowsAffected == 1) selectQuery else none[UserProfileSqlModel].pure[ConnectionIO]
      } yield updatedUser.map { userSql =>
        // Transform UserProfileSqlRetrieval to UserProfile
        UserProfile(
          userId = userSql.userId,
          userLoginDetails =
            UserLoginDetails(
              id = Some(userSql.id),
              userId = userSql.userId,
              username = userSql.username,
              passwordHash = userSql.passwordHash,
              email = userSql.email,
              role = userSql.role,
              createdAt = userSql.createdAt,
              updatedAt = userSql.updatedAt
            ),
          firstName = userSql.firstName,
          lastName = userSql.lastName,
          userAddress =
            UserAddress(
              userId = userSql.userId,
              street = Some(userSql.street),
              city = Some(userSql.city),
              country = Some(userSql.country),
              county = userSql.county,
              postcode = Some(userSql.postcode),
              createdAt = userSql.createdAt,
              updatedAt = userSql.updatedAt
            ),
          contactNumber = userSql.contactNumber,
          email = userSql.email,
          role = userSql.role,
          createdAt = userSql.createdAt,
          updatedAt = userSql.updatedAt
        )
      }

    // Transact and handle errors in the F context
    result.transact(transactor).flatMap {
      case Some(user) => Concurrent[F].pure(Some(user))
      case None => Concurrent[F].pure(None)
    }
  }
}
