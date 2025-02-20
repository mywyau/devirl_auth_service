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
import io.circe.parser.decode
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.desk.deskSpecifications.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.DeskType
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.Logger
import models.OpeningHours

trait DeskSpecificationsRepositoryAlgebra[F[_]] {

  def findByDeskId(deskId: String): F[Option[DeskSpecificationsPartial]]

  def findByOfficeId(officeId: String): F[List[DeskSpecificationsPartial]]

  def create(request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(deskId: String, request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(deskId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class DeskSpecificationsRepositoryImpl[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]) extends DeskSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] = Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val deskTypeMeta: Meta[DeskType] = Meta[String].imap(DeskType.fromString)(_.toString)

  implicit val openingHoursListMeta: Meta[List[OpeningHours]] =
    Meta[String].imap(jsonStr => decode[List[OpeningHours]](jsonStr).getOrElse(Nil))(_.asJson.noSpaces)

  override def findByDeskId(deskId: String): F[Option[DeskSpecificationsPartial]] = {
    val findQuery: F[Option[DeskSpecificationsPartial]] =
      sql"""
         SELECT 
          desk_id,
          desk_name,
          description,
          desk_type,
          quantity,
          features,
          opening_hours,
          rules
         FROM desk_specifications
         WHERE desk_id = $deskId
       """.query[DeskSpecificationsPartial].option.transact(transactor)

    findQuery
  }

  override def findByOfficeId(officeId: String): F[List[DeskSpecificationsPartial]] = {
    val findQuery: F[List[DeskSpecificationsPartial]] =
      sql"""
         SELECT 
          desk_id,
          desk_name,
          description,
          desk_type,
          quantity,
          features,
          opening_hours,
          rules
         FROM desk_specifications
         WHERE office_id = $officeId
       """
        .query[DeskSpecificationsPartial]
        .to[List]
        .transact(transactor)

    findQuery
  }

  override def create(request: UpdateDeskSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      INSERT INTO desk_specifications (
          desk_name,
          description,
          desk_type,
          quantity,
          features,
          opening_hours,
          rules
      ) VALUES (
        ${request.deskName},
        ${request.description},
        ${request.deskType},
        ${request.quantity},
        ${request.features},
        ${request.openingHours.asJson.noSpaces}::jsonb,
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
    deskId: String,
    request: UpdateDeskSpecificationsRequest
  ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE desk_specifications
      SET
        desk_name = ${request.deskName},
        description = ${request.description},
        desk_type = ${request.deskType},
        quantity = ${request.quantity},
        features = ${request.features},
        opening_hours = ${request.openingHours.asJson.noSpaces}::jsonb,
        rules = ${request.rules}
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
         DELETE FROM desk_specifications
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
         DELETE FROM desk_specifications
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

object DeskSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad : Logger](transactor: Transactor[F]): DeskSpecificationsRepositoryImpl[F] =
    new DeskSpecificationsRepositoryImpl[F](transactor)
}
