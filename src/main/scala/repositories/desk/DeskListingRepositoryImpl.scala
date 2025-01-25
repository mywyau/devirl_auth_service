package repositories.desk

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingBusinessAndOffice
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.DeskType
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

trait DeskListingRepositoryAlgebra[F[_]] {

  def getOfficeAndBusinessId(deskId: String): F[Option[DeskListingBusinessAndOffice]]

  def findByDeskId(deskId: String): F[Option[DeskListing]]

  def findAll(officeId: String): F[List[DeskListingCard]]

  def streamAllListingCardDetails(officeId: String): fs2.Stream[F, DeskListingCard]

  def initiate(request: InitiateDeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskListingRepositoryImpl[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]) extends DeskListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val deskTypeMeta: Meta[DeskType] = Meta[String].imap(DeskType.fromString)(_.toString)

  override def getOfficeAndBusinessId(deskId: String): F[Option[DeskListingBusinessAndOffice]] = {
    val fetchOfficeAndBusinessIds =
      sql"""
        SELECT 
          ds.desk_id AS deskId,
          ds.office_id AS officeId,
          ds.business_id AS businessId
        FROM desk_specifications ds
        INNER JOIN desk_pricing dp ON ds.desk_id = dp.desk_id
        WHERE ds.desk_id = $deskId 
          AND ds.office_id = dp.office_id 
          AND ds.business_id = dp.business_id
      """.query[DeskListingBusinessAndOffice].option

    for {
      _ <- Logger[F].info(s"[DeskListingRepositoryImpl][getOfficeAndBusinessId] Attempting to fetch office and business IDs for deskId: $deskId")
      result <- fetchOfficeAndBusinessIds.transact(transactor).flatTap {
        case Some(DeskListingBusinessAndOffice(deskId, officeId, businessId)) =>
          Logger[F].info(s"Successfully retrieved Desk ID: $deskId, Office ID: $officeId, Business ID: $businessId for deskId: $deskId")
        case None =>
          Logger[F].warn(s"No matching office and business IDs found for deskId: $deskId")
      }
    } yield result
  }

  override def findByDeskId(deskId: String): F[Option[DeskListing]] = {
    val fetchDeskDetails =
      sql"""
      SELECT 
        ds.desk_id AS ds_desk_id,
        ds.desk_name AS ds_desk_name,
        ds.description AS ds_description,
        ds.desk_type AS ds_desk_type,
        ds.quantity AS ds_quantity,
        ds.features AS ds_features,
        ds.availability AS ds_availability,
        ds.rules AS ds_rules,

        dp.price_per_hour AS dp_price_per_hour,
        dp.price_per_day AS dp_price_per_day,
        dp.price_per_week AS dp_price_per_week,
        dp.price_per_month AS dp_price_per_month,
        dp.price_per_year AS dp_price_per_year
      FROM desk_specifications ds
      LEFT JOIN desk_pricing dp ON ds.desk_id = dp.desk_id
      WHERE ds.desk_id = $deskId
    """.query[(DeskSpecificationsPartial, RetrievedDeskPricing)].option

    for {
      _ <- Logger[F].info(s"Attempting to fetch desk details for deskId: $deskId")
      result <- fetchDeskDetails.transact(transactor).flatTap {
        case Some((specs, pricing)) =>
          Logger[F].info(s"Desk details retrieved successfully for deskId: $deskId")
        case None =>
          Logger[F].warn(s"No desk found for deskId: $deskId")
      }
      deskListing <- result match {
        case Some((specs, pricing)) =>
          Concurrent[F].pure(Some(DeskListing(deskId = specs.deskId, specifications = specs, pricing = pricing)))
        case None =>
          Concurrent[F].pure(None)
      }
    } yield deskListing
  }

  override def findAll(officeId: String): F[List[DeskListingCard]] = {
    val fetchBasicDeskCardDetails =
      sql"""
      SELECT 
          ds.desk_id AS deskId,
          ds.desk_name AS deskName,
          ds.description AS description
      FROM desk_specifications ds
      INNER JOIN desk_pricing dp ON ds.desk_id = dp.desk_id
      WHERE ds.office_id = $officeId
    """.query[DeskListingCard].to[List]

    for {
      _ <- Logger[F].info(s"Attempting to fetch all desk details for officeId: $officeId")
      result <- fetchBasicDeskCardDetails.transact(transactor)
      _ <-
        if (result.isEmpty) {
          Logger[F].warn(s"No desks found for officeId: $officeId")
        } else {
          Logger[F].info(s"Successfully retrieved ${result.size} desks for officeId: $officeId")
        }
    } yield result
  }

  // Streaming method implementation
  override def streamAllListingCardDetails(officeId: String): fs2.Stream[F, DeskListingCard] = {
    val query =
      sql"""
      SELECT 
        ds.desk_id AS deskId,
        ds.desk_name AS deskName,
        ds.description AS description
      FROM desk_specifications ds
      INNER JOIN desk_pricing dp ON ds.desk_id = dp.desk_id
      WHERE ds.office_id = $officeId
    """.query[DeskListingCard]

    fs2.Stream.eval(Logger[F].info(s"Streaming desk listing cards for officeId: $officeId")) *>
      query.stream.chunkLimit(100).transact(transactor).flatMap(fs2.Stream.chunk)
  }

  override def initiate(request: InitiateDeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val insertDeskSpecifications =
      sql"""
        INSERT INTO desk_specifications (
          business_id,
          office_id,
          desk_id,
          desk_name,
          description
        ) VALUES (
          ${request.businessId},
          ${request.officeId},
          ${request.deskId},
          ${request.deskName},
          ${request.description}
        )
      """

    val insertDeskPricingDetails =
      sql"""
        INSERT INTO desk_pricing (
          business_id,
          office_id,
          desk_id
        ) VALUES (
          ${request.businessId},
          ${request.officeId},
          ${request.deskId}
        )
      """

    (for {
      rowDeskSpecifications <- insertDeskSpecifications.update.run
      rowDeskPricingDetails <- insertDeskPricingDetails.update.run
    } yield rowDeskSpecifications + rowDeskPricingDetails)
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows == 2 =>
          CreateSuccess.validNel
        case Left(e: java.sql.SQLIntegrityConstraintViolationException) =>
          ConstraintViolation.invalidNel
        case Left(e: java.sql.SQLException) =>
          DatabaseError.invalidNel
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
        case _ =>
          UnexpectedResultError.invalidNel
      }
  }

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM desk_specifications
           WHERE desk_id = $deskId
         """.update.run

    val deletePricingQuery =
      sql"""
           DELETE FROM desk_pricing
           WHERE desk_id = $deskId
         """.update.run

    val combinedQuery = for {
      specificationRows <- deleteSpecificationsQuery
      pricingRows <- deletePricingQuery
    } yield specificationRows + pricingRows

    combinedQuery.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows == 2 =>
        DeleteSuccess.validNel
      case Right(affectedRows) if affectedRows < 2 =>
        NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
        ForeignKeyViolationError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
        DatabaseConnectionError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        SqlExecutionError(ex.getMessage).invalidNel
      case Left(ex) =>
        UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      case _ =>
        UnexpectedResultError.invalidNel
    }
  }

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM desk_specifications
           WHERE office_id = $officeId
         """.update.run

    val deletePricingQuery =
      sql"""
           DELETE FROM desk_pricing
           WHERE office_id = $officeId
         """.update.run

    val combinedQuery = for {
      specificationRows <- deleteSpecificationsQuery
      pricingRows <- deletePricingQuery
    } yield specificationRows + pricingRows

    combinedQuery.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows > 0 =>
        DeleteSuccess.validNel
      case Right(affectedRows) if affectedRows == 0 =>
        NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
        ForeignKeyViolationError.invalidNel
      case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
        DatabaseConnectionError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        SqlExecutionError(ex.getMessage).invalidNel
      case Left(ex) =>
        UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
      case _ =>
        UnexpectedResultError.invalidNel
    }
  }
}

object DeskListingRepository {
  def apply[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]): DeskListingRepositoryImpl[F] =
    new DeskListingRepositoryImpl[F](transactor)
}
