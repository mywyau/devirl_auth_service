package repositories.office

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.office.contact_details.errors.OfficeContactDetailsErrors
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails

trait OfficeContactDetailsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeContactDetails]]

  def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]]
}

class OfficeContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByOfficeId(officeId: String): F[Option[OfficeContactDetails]] = {
    val findQuery: F[Option[OfficeContactDetails]] =
      sql"SELECT * FROM office_contact_details WHERE office_id = $officeId".query[OfficeContactDetails].option.transact(transactor)
    findQuery
  }

  override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
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
      case Right(rowsAffected) =>
        if (rowsAffected == 1) {
          rowsAffected.validNel
        } else {
          InsertionFailed.invalidNel
        }
      case Left(e: java.sql.SQLIntegrityConstraintViolationException) =>
        ConstraintViolation.invalidNel
      case Left(e: java.sql.SQLException) =>
        DatabaseError.invalidNel
      case Left(e) =>
        UnknownError(e.getMessage).invalidNel
    }

  override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
       UPDATE office_contact_details
       SET
          primary_contact_first_name = ${request.primaryContactFirstName},
          primary_contact_last_name = ${request.primaryContactLastName},
          contact_email = ${request.contactEmail},
          contact_number = ${request.contactNumber},
          updated_at = ${request.updatedAt}
       WHERE office_id = $officeId
     """.update.run.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows > 0)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex: java.sql.SQLException) =>
        DatabaseError.invalidNel
      case Left(ex) =>
        UnknownError(ex.getMessage).invalidNel
    }

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
        DELETE FROM office_contact_details
        WHERE office_id = $officeId
      """.update
    deleteQuery.run.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows > 0)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex) =>
        DeleteError.invalidNel
    }
  }
}

object OfficeContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeContactDetailsRepositoryAlgebra[F] =
    new OfficeContactDetailsRepositoryImpl[F](transactor)
}
