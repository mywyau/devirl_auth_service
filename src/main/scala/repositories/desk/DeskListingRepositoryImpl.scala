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
import models.desk_listing.DeskListingPartial
import models.desk_listing.requests.DeskListingRequest
import models.desk_listing.DeskType

trait DeskListingRepositoryAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[DeskListingPartial]]

  def findByOfficeId(officeId: String): F[List[DeskListingPartial]]

  def create(request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(deskId: String, request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends DeskListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val deskTypeMeta: Meta[DeskType] = Meta[String].imap(DeskType.fromString)(_.toString)

  override def findByDeskId(deskId: String): F[Option[DeskListingPartial]] = {
    val findQuery: F[Option[DeskListingPartial]] =
      sql"""
         SELECT 
          desk_name,
          description,
          desk_type,
          quantity,
          price_per_hour,
          price_per_day,
          features,
          availability,
          rules
         FROM desk_listings
         WHERE desk_id = $deskId
       """.query[DeskListingPartial].option.transact(transactor)

    findQuery
  }

  override def findByOfficeId(officeId: String): F[List[DeskListingPartial]] = {
    val findQuery: F[List[DeskListingPartial]] =
      sql"""
         SELECT 
          desk_name,
          description,
          desk_type,
          quantity,
          price_per_hour,
          price_per_day,
          features,
          availability,
          rules
         FROM desk_listings
         WHERE office_id = $officeId
       """.query[DeskListingPartial].to[List].transact(transactor)

    findQuery
  }

  override def create(request: DeskListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      INSERT INTO desk_listings (
          desk_name,
          description,
          desk_type,
          quantity,
          price_per_hour,
          price_per_day,
          features,
          availability,
          rules
      ) VALUES (
        ${request.deskName},
        ${request.description},
        ${request.deskType},
        ${request.quantity},
        ${request.pricePerHour},
        ${request.pricePerDay},
        ${request.features},
        ${request.availability.asJson.noSpaces}::jsonb,
        ${request.rules}
      )
    """.update.run.transact(transactor).attempt.map {
      case Right(affectedRows) if affectedRows == 1 =>
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

  override def update(
    officeId: String,
    request: DeskListingRequest
  ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE desk_listings
      SET
        desk_name = ${request.deskName},
        description = ${request.description},
        desk_type = ${request.deskType},
        quantity = ${request.quantity},
        price_per_hour = ${request.pricePerHour},
        price_per_day = ${request.pricePerDay},
        features = ${request.features},
        availability = ${request.availability.asJson.noSpaces}::jsonb,
        rules = ${request.rules},
      WHERE office_id = ${officeId}
    """.update.run
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows == 1 =>
          UpdateSuccess.validNel
        case Right(affectedRows) if affectedRows == 0 =>
          NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23503" =>
          ForeignKeyViolationError.invalidNel // Foreign key constraint violation
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "08001" =>
          DatabaseConnectionError.invalidNel // Database connection issue
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "22001" =>
          DataTooLongError.invalidNel // Data length exceeds column limit
        case Left(ex: java.sql.SQLException) =>
          SqlExecutionError(ex.getMessage).invalidNel // General SQL execution error
        case Left(ex) =>
          UnknownError(s"Unexpected error: ${ex.getMessage}").invalidNel
        case _ =>
          UnexpectedResultError.invalidNel
      }

  override def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM desk_listings
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
         DELETE FROM desk_listings
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

object DeskListingRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): DeskListingRepositoryImpl[F] =
    new DeskListingRepositoryImpl[F](transactor)
}
