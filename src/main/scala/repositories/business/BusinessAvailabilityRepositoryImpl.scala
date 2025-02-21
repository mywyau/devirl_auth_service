package repositories.business

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import java.sql.Time
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.availability.*
import models.database.*
import models.Day

trait BusinessAvailabilityRepositoryAlgebra[F[_]] {

  def findAvailabilityForBusiness(businessId: String): F[List[RetrieveSingleBusinessAvailability]]

  def findAvailabilityForDay(businessId: String, weekDay: Day): F[Option[RetrieveSingleBusinessAvailability]]

  def createDaysOpen(request: CreateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateDaysOpen(request: UpdateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def createOpeningHours(request: CreateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def updateOpeningHours(request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String, day: Day): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAll(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAvailabilityRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAvailabilityRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val dayMeta: Meta[Day] = Meta[String].timap(Day.fromString)(_.toString)

  implicit val localTimeMeta: Meta[LocalTime] =
    Meta[Time].timap(_.toLocalTime)(Time.valueOf)

  override def findAvailabilityForBusiness(businessId: String): F[List[RetrieveSingleBusinessAvailability]] = {
    val findQuery =
      sql"""
       SELECT
        weekday,
        opening_time,
        closing_time
       FROM business_opening_hours
       WHERE business_id = $businessId
     """.query[RetrieveSingleBusinessAvailability].to[List].transact(transactor)

    findQuery
  }

  override def findAvailabilityForDay(businessId: String, weekDay: Day): F[Option[RetrieveSingleBusinessAvailability]] = {
    val findQuery =
      sql"""
         SELECT
          weekday,
          opening_time,
          closing_time
         FROM business_opening_hours
         WHERE business_id = $businessId
         AND weekday = ${weekDay.toString}
       """.query[RetrieveSingleBusinessAvailability].option.transact(transactor)

    findQuery
  }

  override def createDaysOpen(request: CreateBusinessDaysRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {

    val insertNew = Update[(String, String, Day)](
      "INSERT INTO business_opening_hours (user_id, business_id, weekday) VALUES (?, ?, ?)"
    ).updateMany(request.days.map(day => (request.userId, request.businessId, day))) // Use request.businessId

    insertNew
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows > 0 => CreateSuccess.validNel
        case Right(_) => NotFoundError.invalidNel
        case Left(ex: java.sql.SQLException) if ex.getSQLState == "23505" =>
          ConstraintViolation.invalidNel // Handle unique constraint violations
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
      }
  }

  override def updateDaysOpen(
    request: UpdateBusinessDaysRequest
  ): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {

    val deleteExisting =
      sql"""
        DELETE FROM business_opening_hours WHERE business_id = ${request.businessId} AND user_id = ${request.userId}
      """.update.run

    val insertNew = Update[(String, String, Day)](
      "INSERT INTO business_opening_hours (user_id, business_id, weekday) VALUES (?, ?, ?)"
    ).updateMany(request.days.map(day => (request.userId, request.businessId, day)))

    (for {
      _ <- deleteExisting
      affectedRows <- insertNew
    } yield affectedRows)
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows > 0 => UpdateSuccess.validNel
        case Right(_) => NotFoundError.invalidNel
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
      }
  }

  override def createOpeningHours(request: CreateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

  override def updateOpeningHours(request: UpdateBusinessOpeningHoursRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE business_opening_hours
      SET
        opening_time = COALESCE(${request.openingTime}, business_opening_hours.opening_time),
        closing_time = COALESCE(${request.closingTime}, business_opening_hours.closing_time),
        updated_at = CURRENT_TIMESTAMP
      WHERE business_id = ${request.businessId}
        AND weekday = ${request.day.toString}
    """.update.run
      .transact(transactor)
      .attempt
      .map {
        case Right(affectedRows) if affectedRows > 0 => UpdateSuccess.validNel
        case Right(_) => NotFoundError.invalidNel
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
      }

  override def delete(businessId: String, day: Day): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
      DELETE FROM business_opening_hours
      WHERE business_id = $businessId
      AND weekday = ${day.toString}
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

  override def deleteAll(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM business_opening_hours
         WHERE business_id = $businessId
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

object BusinessAvailabilityRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessAvailabilityRepositoryImpl[F] =
    new BusinessAvailabilityRepositoryImpl[F](transactor)
}
