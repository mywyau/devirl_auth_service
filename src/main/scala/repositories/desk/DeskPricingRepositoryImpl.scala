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
import models.desk.deskPricing.UpdateDeskPricingRequest
import models.desk.deskPricing.RetrievedDeskPricing
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger

trait DeskPricingRepositoryAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[RetrievedDeskPricing]]

  def findByOfficeId(officeId: String): F[List[RetrievedDeskPricing]]

  def update(deskId: String, request: UpdateDeskPricingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskPricingRepositoryImpl[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]) extends DeskPricingRepositoryAlgebra[F] {

  override def findByDeskId(deskId: String): F[Option[RetrievedDeskPricing]] = {
    val findQuery: F[Option[RetrievedDeskPricing]] =
      sql"""
         SELECT 
          price_per_hour,
          price_per_day,
          price_per_week,
          price_per_month,
          price_per_year
         FROM desk_pricing
         WHERE desk_id = $deskId
       """.query[RetrievedDeskPricing].option.transact(transactor)

    findQuery
  }

  override def findByOfficeId(officeId: String): F[List[RetrievedDeskPricing]] = {
    val findQuery: F[List[RetrievedDeskPricing]] =
      sql"""
         SELECT 
          price_per_hour,
          price_per_day,
          price_per_week,
          price_per_month,
          price_per_year
         FROM desk_pricing
         WHERE office_id = $officeId
       """
        .query[RetrievedDeskPricing]
        .to[List]
        .transact(transactor)

    findQuery
  }

  override def update(
    deskId: String,
    request: UpdateDeskPricingRequest
  ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE desk_pricing
      SET
        price_per_hour = ${request.pricePerHour},
        price_per_day = ${request.pricePerDay},
        price_per_week = ${request.pricePerWeek},
        price_per_month = ${request.pricePerMonth},
        price_per_year = ${request.pricePerYear}
      WHERE desk_id = ${deskId}
    """.update.run
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows == 1 =>
          UpdateSuccess.validNel
        case Right(affectedRows) if affectedRows == 0 =>
          NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
          ForeignKeyViolationError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
          DatabaseConnectionError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "22001" =>
          DataTooLongError.invalidNel
        case Left(ex: java.sql.SQLException) =>
          SqlExecutionError(ex.getMessage).invalidNel
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
        case _ =>
          UnexpectedResultError.invalidNel
      }

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM desk_pricing
         WHERE desk_id = $deskId
       """.update

    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows == 1 =>
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

  override def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM desk_pricing
         WHERE office_id = $officeId
       """.update

    deleteQuery.run.transact(transactor).attempt.map {
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

object DeskPricingRepository {
  def apply[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]): DeskPricingRepositoryImpl[F] =
    new DeskPricingRepositoryImpl[F](transactor)
}
