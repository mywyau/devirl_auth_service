package repositories.business

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.business.adts.DeskType
import models.business.business_desk.service.BusinessDesk
import models.users.adts.Role

import java.sql.Timestamp
import java.time.LocalDateTime
import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.business.business_address.service.BusinessAddress
import models.business.business_desk.errors.BusinessDeskErrors
import models.business.business_desk.requests.BusinessDeskRequest
import models.users.adts.Role

import java.sql.Timestamp
import java.time.LocalDateTime


trait BusinessDeskRepositoryAlgebra[F[_]] {

  def createDeskToRent(user: BusinessDeskRequest): F[Int]

  def findByUserId(userId: String): F[Option[BusinessDesk]]

  def updateDesk(
                  id: Option[Int],
                  business_id: String,
                  workspace_id: String,
                  title: String,
                  description: Option[String],
                  desk_type: DeskType,
                  price_per_hour: BigDecimal,
                  price_per_day: BigDecimal,
                  rules: Option[String],
                  created_at: LocalDateTime,
                  updated_at: LocalDateTime
                ): F[Option[BusinessDesk]]
}

class BusinessDeskRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessDeskRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val deskTypeMeta: Meta[DeskType] = Meta[String].imap(DeskType.fromString)(_.toString)

  override def findByUserId(userId: String): F[Option[BusinessDesk]] = {
    val findQuery: F[Option[BusinessDesk]] =
      sql"SELECT * FROM business_desk WHERE business_id = $userId"
        .query[BusinessDesk]
        .option
        .transact(transactor)
    findQuery
  }

  override def createDeskToRent(businessDesk: BusinessDeskRequest): F[Int] = {
    sql"""
      INSERT INTO business_desk (
        business_id,
        workspace_id,
        title,
        description,
        desk_type,
        price_per_hour,
        price_per_day,
        rules,
        created_at,
        updated_at   
      )
      VALUES (
        ${businessDesk.business_id},
        ${businessDesk.workspace_id},
        ${businessDesk.title},
        ${businessDesk.description},
        ${businessDesk.desk_type},
        ${businessDesk.price_per_hour},
        ${businessDesk.price_per_day},
        ${businessDesk.rules},
        ${businessDesk.created_at},
        ${businessDesk.updated_at}
        )
    """.update
      .run
      .transact(transactor)
  }

  override def updateDesk(
                           id: Option[Int],
                           business_id: String,
                           workspace_id: String,
                           title: String,
                           description: Option[String],
                           desk_type: DeskType,
                           price_per_hour: BigDecimal,
                           price_per_day: BigDecimal,
                           rules: Option[String],
                           created_at: LocalDateTime,
                           updated_at: LocalDateTime
                         ): F[Option[BusinessDesk]] = ???

  //  override def updateDesk(
  //                           id: Option[Int],
  //                           business_id: String,
  //                           workspace_id: String,
  //                           title: String,
  //                           description: Option[String],
  //                           desk_type: DeskType,
  //                           price_per_hour: BigDecimal,
  //                           price_per_day: BigDecimal,
  //                           rules: Option[String],
  //                           created_at: LocalDateTime,
  //                           updated_at: LocalDateTime
  //                         ): F[Option[BusinessDesk]] = {
  //
  //    // Dynamically build the update query
  //    val updates = List(
  //      street.map(s => fr"street = $s"),
  //      city.map(c => fr"city = $c"),
  //      country.map(c => fr"country = $c"),
  //      county.map(c => fr"county = $c"),
  //      postcode.map(p => fr"postcode = $p")
  //      postcode.map(p => fr"postcode = $p")
  //      postcode.map(p => fr"postcode = $p")
  //      postcode.map(p => fr"postcode = $p")
  //      postcode.map(p => fr"postcode = $p")
  //      postcode.map(p => fr"postcode = $p")
  //    ).flatten
  //
  //    val updateQuery: Option[ConnectionIO[Int]] =
  //      if (updates.nonEmpty) {
  //        (fr"UPDATE business_desk SET" ++ updates.intercalate(fr",") ++
  //          fr"WHERE user_id = $userId").update.run.some
  //      } else None
  //
  //    val selectQuery: ConnectionIO[Option[BusinessDesk]] =
  //      sql"""
  //            SELECT id, user_id, street, city, country, county, postcode, created_at, updated_at
  //            FROM business_desk
  //            WHERE user_id = $userId
  //          """.query[BusinessDesk].option
  //
  //    val result: ConnectionIO[Option[BusinessDesk]] = updateQuery match {
  //      case Some(query) =>
  //        for {
  //          rowsAffected <- query
  //          updatedDesk <- if (rowsAffected > 0) selectQuery else none[BusinessDesk].pure[ConnectionIO]
  //        } yield updatedDesk
  //      case None =>
  //        selectQuery // If no updates, return the existing Desk
  //    }
  //
  //    result.transact(transactor)
  //  }

}


object BusinessDeskRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessDeskRepositoryImpl[F] =
    new BusinessDeskRepositoryImpl[F](transactor)
}
