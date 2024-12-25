package repositories.office

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import models.database.*
import models.office.adts.OfficeType
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecifications

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeSpecificationsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeSpecifications]]

  def createSpecs(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[SqlErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]

}

class OfficeSpecificationsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findByOfficeId(officeId: String): F[Option[OfficeSpecifications]] = {
    val findQuery: F[Option[OfficeSpecifications]] =
      sql"SELECT * FROM office_specs WHERE office_id = $officeId"
        .query[OfficeSpecifications]
        .option
        .transact(transactor)
    findQuery
  }

  override def createSpecs(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[SqlErrors, Int]] = {
    sql"""
      INSERT INTO office_specs (
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
    """
      .update
      .run
      .transact(transactor)
      .attempt
      .map {
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
          UnknownError.invalidNel
      }
  }


  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
          DELETE FROM office_specs
          WHERE office_id = $officeId
        """.update

    deleteQuery
      .run
      .transact(transactor)
      .attempt
      .map {
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


object OfficeSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeSpecificationsRepositoryAlgebra[F] =
    new OfficeSpecificationsRepositoryImpl[F](transactor)
}
