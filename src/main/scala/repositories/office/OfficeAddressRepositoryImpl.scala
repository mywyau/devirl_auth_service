package repositories.office

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial

trait OfficeAddressRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, OfficeAddressPartial]]

  def create(request: CreateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, request: UpdateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

}

class OfficeAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, OfficeAddressPartial]] = {

    def validateOfficeId(officeId: String): ValidatedNel[DatabaseErrors, String] =
      if (officeId.nonEmpty) officeId.validNel
      else DatabaseError.invalidNel

    val findQuery: ConnectionIO[Option[OfficeAddressPartial]] =
      sql"""
       SELECT 
           business_id,
           office_id,
           building_name,
           floor_number,
           street,
           city,
           country,
           county,
           postcode,
           latitude,
           longitude
       FROM office_address
       WHERE office_id = $officeId
      """.query[OfficeAddressPartial].option

    def handleDatabaseError(ex: Throwable): DatabaseErrors = ex match {
      case _: java.sql.SQLIntegrityConstraintViolationException =>
        ConstraintViolation
      case _: java.sql.SQLException =>
        SqlExecutionError(s"SQL error occurred while querying office_id: $officeId: ${ex.getMessage}")
      case _ =>
        UnknownError(s"Unexpected error occurred: ${ex.getMessage}")
    }

    validateOfficeId(officeId) match {
      case Valid(validOfficeId) =>
        findQuery.transact(transactor).attempt.map {
          case Right(Some(officeAddress)) =>
            officeAddress.validNel
          case Right(None) =>
            NotFoundError.invalidNel
          case Left(ex) =>
            handleDatabaseError(ex).invalidNel
        }
      case Invalid(validationErrors) =>
        validationErrors.invalid.pure[F]
    }
  }

  override def create(request: CreateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      INSERT INTO office_address (
        business_id,
        office_id,
        building_name,
        floor_number,
        street,
        city,
        country,
        county,
        postcode,
        latitude,
        longitude
      ) VALUES (
        ${request.businessId},
        ${request.officeId},
        ${request.buildingName},
        ${request.floorNumber},
        ${request.street},
        ${request.city},
        ${request.country},
        ${request.county},
        ${request.postcode},
        ${request.latitude},
        ${request.longitude}
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

  override def update(officeId: String, request: UpdateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
    UPDATE office_address
    SET
        building_name = ${request.buildingName},
        floor_number = ${request.floorNumber},
        street = ${request.street},
        city = ${request.city},
        country = ${request.country},
        county = ${request.county},
        postcode = ${request.postcode},
        latitude = ${request.latitude},
        longitude = ${request.longitude},
        updated_at = ${LocalDateTime.now()}
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

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM office_address
         WHERE office_id = $officeId
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

  override def deleteAllByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteQuery: Update0 =
      sql"""
         DELETE FROM office_address
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

object OfficeAddressRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeAddressRepositoryAlgebra[F] =
    new OfficeAddressRepositoryImpl[F](transactor)
}
