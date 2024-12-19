package repositories.desk

import cats.Monad
import cats.effect.Concurrent
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import models.business.adts.DeskType
import models.business.desk_listing.requests.DeskListingRequest
import models.business.desk_listing.service.DeskListing

import java.sql.Timestamp
import java.time.LocalDateTime


trait DeskListingRepositoryAlgebra[F[_]] {

  def createDeskToRent(user: DeskListingRequest): F[Int]

  def findByUserId(userId: String): F[Option[DeskListing]]

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
                ): F[Option[DeskListing]]
}

class DeskListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends DeskListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val deskTypeMeta: Meta[DeskType] = Meta[String].imap(DeskType.fromString)(_.toString)

  override def findByUserId(business_id: String): F[Option[DeskListing]] = {
    sql"SELECT * FROM desk_listings WHERE business_id = $business_id"
      .query[DeskListing] // Ensure implicit Read[DeskListing] is available
      .option
      .transact(transactor)
  }


  override def createDeskToRent(deskListing: DeskListingRequest): F[Int] = {
    sql"""
         INSERT INTO desk_listings (
           business_id,
           workspace_id,
           title,
           description,
           desk_type,
           quantity,
           price_per_hour,
           price_per_day,
           features,
           availability,
           rules,
           created_at,
           updated_at
         ) VALUES (
         ${deskListing.business_id},
         ${deskListing.workspace_id},
         ${deskListing.title},
         ${deskListing.description},
         ${deskListing.desk_type},
         ${deskListing.quantity},
         ${deskListing.price_per_hour},
         ${deskListing.price_per_day},
         ${deskListing.features},
         ${deskListing.availability.asJson.noSpaces}::jsonb,
         ${deskListing.rules},
         ${deskListing.created_at},
         ${deskListing.updated_at}
         )
       """
      .update
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
                         ): F[Option[DeskListing]] = ???

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
  //                         ): F[Option[DeskListing]] = {
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
  //    val selectQuery: ConnectionIO[Option[DeskListing]] =
  //      sql"""
  //            SELECT id, user_id, street, city, country, county, postcode, created_at, updated_at
  //            FROM business_desk
  //            WHERE user_id = $userId
  //          """.query[DeskListing].option
  //
  //    val result: ConnectionIO[Option[DeskListing]] = updateQuery match {
  //      case Some(query) =>
  //        for {
  //          rowsAffected <- query
  //          updatedDesk <- if (rowsAffected > 0) selectQuery else none[DeskListing].pure[ConnectionIO]
  //        } yield updatedDesk
  //      case None =>
  //        selectQuery // If no updates, return the existing Desk
  //    }
  //
  //    result.transact(transactor)
  //  }

}


object DeskListingRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): DeskListingRepositoryImpl[F] =
    new DeskListingRepositoryImpl[F](transactor)
}
