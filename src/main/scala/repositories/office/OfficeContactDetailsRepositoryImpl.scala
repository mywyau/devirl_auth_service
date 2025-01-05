package repositories.office

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.database.*
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.contact_details.errors.OfficeContactDetailsErrors
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeContactDetailsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeContactDetailsPartial]]

  def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByOfficeId(officeId: String): F[Option[OfficeContactDetailsPartial]] = {
    val findQuery: F[Option[OfficeContactDetailsPartial]] =
      sql"""
         SELECT 
            business_id,
            office_id,
            primary_contact_first_name,
            primary_contact_last_name,
            contact_email,
            contact_number
         FROM office_contact_details
         WHERE office_id = $officeId
       """.query[OfficeContactDetailsPartial].option.transact(transactor)

    findQuery
  }

  override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
        INSERT INTO office_contact_details (
          business_id,
          office_id,
          primary_contact_first_name,
          primary_contact_last_name,
          contact_email,
          contact_number
        ) VALUES (
          ${createOfficeContactDetailsRequest.businessId},
          ${createOfficeContactDetailsRequest.officeId},
          ${createOfficeContactDetailsRequest.primaryContactFirstName},
          ${createOfficeContactDetailsRequest.primaryContactLastName},
          ${createOfficeContactDetailsRequest.contactEmail},
          ${createOfficeContactDetailsRequest.contactNumber}
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

  override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
       UPDATE office_contact_details
       SET
          primary_contact_first_name = ${request.primaryContactFirstName},
          primary_contact_last_name = ${request.primaryContactLastName},
          contact_email = ${request.contactEmail},
          contact_number = ${request.contactNumber},
          updated_at = ${request.updatedAt}
       WHERE office_id = $officeId
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
         DELETE FROM office_contact_details
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
         DELETE FROM office_contact_details
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

object OfficeContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeContactDetailsRepositoryAlgebra[F] =
    new OfficeContactDetailsRepositoryImpl[F](transactor)
}
