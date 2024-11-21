package repositories.business

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.business.business_address.service.BusinessAddress
import models.users.adts.Role

import java.sql.Timestamp
import java.time.LocalDateTime


trait BusinessAddressRepositoryAlgebra[F[_]] {

  def createRegistrationBusinessAddress(userId: String): F[Int]

  def createUserAddress(user: BusinessAddress): F[Int]

  def findByUserId(userId: String): F[Option[BusinessAddress]]

  def updateAddressDynamic(
                            userId: String,
                            street: Option[String],
                            city: Option[String],
                            country: Option[String],
                            county: Option[String],
                            postcode: Option[String]
                          ): F[Option[BusinessAddress]]
}

class BusinessAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def findByUserId(userId: String): F[Option[BusinessAddress]] = {
    val findQuery: F[Option[BusinessAddress]] =
      sql"SELECT * FROM business_address WHERE user_id = $userId"
        .query[BusinessAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def createRegistrationBusinessAddress(userId: String): F[Int] = {
    sql"""
      INSERT INTO business_address (
        user_id
      )
      VALUES (
        $userId
        )
    """.update
      .run
      .transact(transactor)
  }

  override def createUserAddress(businessAddress: BusinessAddress): F[Int] = {
    sql"""
      INSERT INTO business_address (
        user_id,
        street,
        city,
        country,
        county,
        postcode,
        created_at,
        updated_at
      )
      VALUES (
        ${businessAddress.userId},
        ${businessAddress.street},
        ${businessAddress.city},
        ${businessAddress.country},
        ${businessAddress.county},
        ${businessAddress.postcode},
        ${businessAddress.createdAt},
        ${businessAddress.updatedAt}
        )
    """.update
      .run
      .transact(transactor)
  }

  override def updateAddressDynamic(
                                     userId: String,
                                     street: Option[String],
                                     city: Option[String],
                                     country: Option[String],
                                     county: Option[String],
                                     postcode: Option[String]
                                   ): F[Option[BusinessAddress]] = {

    // Dynamically build the update query
    val updates = List(
      street.map(s => fr"street = $s"),
      city.map(c => fr"city = $c"),
      country.map(c => fr"country = $c"),
      county.map(c => fr"county = $c"),
      postcode.map(p => fr"postcode = $p")
    ).flatten

    val updateQuery: Option[ConnectionIO[Int]] =
      if (updates.nonEmpty) {
        (fr"UPDATE business_address SET" ++ updates.intercalate(fr",") ++
          fr"WHERE user_id = $userId").update.run.some
      } else None

    val selectQuery: ConnectionIO[Option[BusinessAddress]] =
      sql"""
          SELECT id, user_id, street, city, country, county, postcode, created_at, updated_at
          FROM business_address
          WHERE user_id = $userId
        """.query[BusinessAddress].option

    val result: ConnectionIO[Option[BusinessAddress]] = updateQuery match {
      case Some(query) =>
        for {
          rowsAffected <- query
          updatedAddress <- if (rowsAffected > 0) selectQuery else none[BusinessAddress].pure[ConnectionIO]
        } yield updatedAddress
      case None =>
        selectQuery // If no updates, return the existing address
    }

    result.transact(transactor)
  }

}


object BusinessAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessAddressRepositoryImpl[F] =
    new BusinessAddressRepositoryImpl[F](transactor)
}
