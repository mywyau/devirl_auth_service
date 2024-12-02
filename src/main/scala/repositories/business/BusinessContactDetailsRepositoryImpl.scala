package repositories.business

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.database.*
import models.business.business_contact_details.errors.BusinessContactDetailsErrors
import models.business.business_contact_details.BusinessContactDetails

import java.sql.Timestamp
import java.time.LocalDateTime

trait BusinessContactDetailsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessContactDetails]]

  def createContactDetails(businessContactDetails: BusinessContactDetails): F[ValidatedNel[SqlErrors, Int]]
}

class BusinessContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessContactDetails]] = {
    val findQuery: F[Option[BusinessContactDetails]] =
      sql"SELECT * FROM business_contact_details WHERE business_id = $businessId"
        .query[BusinessContactDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def createContactDetails(businessContactDetails: BusinessContactDetails): F[ValidatedNel[SqlErrors, Int]] = {
    sql"""
      INSERT INTO business_contact_details (
        business_id,
        business_id,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        created_at,
        updated_at
      ) VALUES (
        ${businessContactDetails.businessId},
        ${businessContactDetails.businessId},
        ${businessContactDetails.primaryContactFirstName},
        ${businessContactDetails.primaryContactLastName},
        ${businessContactDetails.contactEmail},
        ${businessContactDetails.contactNumber},
        ${businessContactDetails.createdAt},
        ${businessContactDetails.updatedAt}
      )
    """.update
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

}


object BusinessContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessContactDetailsRepositoryAlgebra[F] =
    new BusinessContactDetailsRepositoryImpl[F](transactor)
}
