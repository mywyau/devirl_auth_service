package repositories.office

import cats.data.ValidatedNel
import cats.effect.Concurrent
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
import models.office.adts.OfficeType
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial

trait OfficeSpecificationsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeSpecificationsPartial]]

  def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteAllByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeSpecificationsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findByOfficeId(officeId: String): F[Option[OfficeSpecificationsPartial]] = {
    val findQuery: F[Option[OfficeSpecificationsPartial]] =
      sql"""
         SELECT 
            business_id,
            office_id,
            office_name,
            description,
            office_type,
            number_of_floors,
            total_desks,
            capacity,
            amenities,
            availability,
            rules
         FROM office_specifications
         WHERE office_id = $officeId
       """.query[OfficeSpecificationsPartial].option.transact(transactor)

    findQuery
  }

  override def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      INSERT INTO office_specifications (
        business_id,
        office_id,
        office_name,
        description,
        office_type,
        number_of_floors,
        total_desks,
        capacity,
        amenities,
        availability,
        rules
      ) VALUES (
        ${createOfficeSpecificationsRequest.businessId},
        ${createOfficeSpecificationsRequest.officeId},
        ${createOfficeSpecificationsRequest.officeName},
        ${createOfficeSpecificationsRequest.description},
        ${createOfficeSpecificationsRequest.officeType},
        ${createOfficeSpecificationsRequest.numberOfFloors},
        ${createOfficeSpecificationsRequest.totalDesks},
        ${createOfficeSpecificationsRequest.capacity},
        ${createOfficeSpecificationsRequest.amenities},
        ${createOfficeSpecificationsRequest.availability.asJson.noSpaces}::jsonb,
        ${createOfficeSpecificationsRequest.rules}
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

  override def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    sql"""
      UPDATE office_specifications
      SET
        office_name = ${request.officeName},
        description = ${request.description},
        office_type = ${request.officeType},
        number_of_floors = ${request.numberOfFloors},
        total_desks = ${request.totalDesks},
        capacity = ${request.capacity},
        amenities = ${request.amenities},
        availability = ${request.availability},
        rules = ${request.rules},
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
         DELETE FROM office_specifications
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
         DELETE FROM office_specifications
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

object OfficeSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeSpecificationsRepositoryAlgebra[F] =
    new OfficeSpecificationsRepositoryImpl[F](transactor)
}
