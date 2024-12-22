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
import models.office.specifications.OfficeSpecs

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeSpecsRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeSpecs]]

  def createSpecs(user: OfficeSpecs): F[ValidatedNel[SqlErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]

}

class OfficeSpecsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeSpecsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findByOfficeId(officeId: String): F[Option[OfficeSpecs]] = {
    val findQuery: F[Option[OfficeSpecs]] =
      sql"SELECT * FROM office_specs WHERE office_id = $officeId"
        .query[OfficeSpecs]
        .option
        .transact(transactor)
    findQuery
  }

  override def createSpecs(officeSpecs: OfficeSpecs): F[ValidatedNel[SqlErrors, Int]] = {
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
        rules,
        created_at,
        updated_at
      ) VALUES (
        ${officeSpecs.businessId},
        ${officeSpecs.officeId},
        ${officeSpecs.officeName},
        ${officeSpecs.description},
        ${officeSpecs.officeType},
        ${officeSpecs.numberOfFloors},
        ${officeSpecs.totalDesks},
        ${officeSpecs.capacity},
        ${officeSpecs.amenities},
        ${officeSpecs.availability.asJson.noSpaces}::jsonb,
        ${officeSpecs.rules},
        ${officeSpecs.createdAt},
        ${officeSpecs.updatedAt}
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


object OfficeSpecsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeSpecsRepositoryAlgebra[F] =
    new OfficeSpecsRepositoryImpl[F](transactor)
}
