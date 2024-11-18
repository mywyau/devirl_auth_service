package repositories.users

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.adts.Role
import models.users.wanderer_address.service.WandererAddress

import java.sql.Timestamp
import java.time.LocalDateTime


trait WandererAddressRepositoryAlgebra[F[_]] {

  def createRegistrationWandererAddress(userId: String): F[Int]

  def createUserAddress(user: WandererAddress): F[Int]

  def findByUserId(userId: String): F[Option[WandererAddress]]

  def updateAddressDynamic(
                            userId: String,
                            street: Option[String],
                            city: Option[String],
                            country: Option[String],
                            county: Option[String],
                            postcode: Option[String]
                          ): F[Option[WandererAddress]]
}

class WandererAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends WandererAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def findByUserId(userId: String): F[Option[WandererAddress]] = {
    val findQuery: F[Option[WandererAddress]] =
      sql"SELECT * FROM wanderer_address WHERE user_id = $userId"
        .query[WandererAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def createRegistrationWandererAddress(userId: String): F[Int] = {
    sql"""
      INSERT INTO wanderer_address (
        user_id
      )
      VALUES (
        $userId
        )
    """.update
      .run
      .transact(transactor)
  }

  override def createUserAddress(wandererAddress: WandererAddress): F[Int] = {
    sql"""
      INSERT INTO wanderer_address (
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
        ${wandererAddress.userId},
        ${wandererAddress.street},
        ${wandererAddress.city},
        ${wandererAddress.country},
        ${wandererAddress.county},
        ${wandererAddress.postcode},
        ${wandererAddress.createdAt},
        ${wandererAddress.updatedAt}
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
                                   ): F[Option[WandererAddress]] = {

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
        (fr"UPDATE wanderer_address SET" ++ updates.intercalate(fr",") ++
          fr"WHERE user_id = $userId").update.run.some
      } else None

    val selectQuery: ConnectionIO[Option[WandererAddress]] =
      sql"""
          SELECT id, user_id, street, city, country, county, postcode, created_at, updated_at
          FROM wanderer_address
          WHERE user_id = $userId
        """.query[WandererAddress].option

    val result: ConnectionIO[Option[WandererAddress]] = updateQuery match {
      case Some(query) =>
        for {
          rowsAffected <- query
          updatedAddress <- if (rowsAffected > 0) selectQuery else none[WandererAddress].pure[ConnectionIO]
        } yield updatedAddress
      case None =>
        selectQuery // If no updates, return the existing address
    }

    result.transact(transactor)
  }

}


object WandererAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): WandererAddressRepositoryImpl[F] =
    new WandererAddressRepositoryImpl[F](transactor)
}
