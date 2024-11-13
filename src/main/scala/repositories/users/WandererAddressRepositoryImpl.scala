package repositories.users

import cats.Monad
import cats.effect.Concurrent
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.adts.Role
import models.users.wanderer_address.service.WandererAddress

import java.sql.Timestamp
import java.time.LocalDateTime


trait WandererAddressRepositoryAlgebra[F[_]] {

  def createUserAddress(user: WandererAddress): F[Int]

  def findByUserId(userId: String): F[Option[WandererAddress]]
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
        ${wandererAddress.user_id},
        ${wandererAddress.street},
        ${wandererAddress.city},
        ${wandererAddress.country},
        ${wandererAddress.county},
        ${wandererAddress.postcode},
        ${wandererAddress.created_at},
        ${wandererAddress.updated_at}
        )
    """.update
      .run
      .transact(transactor)
  }

}


object WandererAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): WandererAddressRepositoryImpl[F] =
    new WandererAddressRepositoryImpl[F](transactor)
}
