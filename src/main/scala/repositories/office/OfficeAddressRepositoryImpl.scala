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
import models.office.address_details.OfficeAddress
import models.office.address_details.errors.OfficeAddressErrors

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeAddressRepositoryAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[Option[OfficeAddress]]

  def create(officeAddress: OfficeAddress): F[ValidatedNel[SqlErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]

}

class OfficeAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByOfficeId(officeId: String): F[Option[OfficeAddress]] = {
    val findQuery: F[Option[OfficeAddress]] =
      sql"SELECT * FROM office_address WHERE office_id = $officeId"
        .query[OfficeAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def create(officeAddress: OfficeAddress): F[ValidatedNel[SqlErrors, Int]] = {
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
        longitude,
        created_at,
        updated_at
      ) VALUES (
        ${officeAddress.businessId},
        ${officeAddress.officeId},
        ${officeAddress.buildingName},
        ${officeAddress.floorNumber},
        ${officeAddress.street},
        ${officeAddress.city},
        ${officeAddress.country},
        ${officeAddress.county},
        ${officeAddress.postcode},
        ${officeAddress.latitude},
        ${officeAddress.longitude},
        ${officeAddress.createdAt},
        ${officeAddress.updatedAt}
      )
    """.update
      .run
      .transact(transactor)
      .attempt // Capture potential errors
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
           DELETE FROM office_address
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


object OfficeAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeAddressRepositoryAlgebra[F] =
    new OfficeAddressRepositoryImpl[F](transactor)
}
