package repositories.business

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
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.requests.UpdateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.database.*

trait BusinessContactDetailsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessContactDetails]]

  def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]
}

class BusinessContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessContactDetails]] = {
    val findQuery: F[Option[BusinessContactDetails]] =
      sql"SELECT * FROM business_contact_details WHERE business_id = $businessId".query[BusinessContactDetails].option.transact(transactor)
    findQuery
  }

  override def create(createBusinessContactDetailsRequest: CreateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      INSERT INTO business_contact_details (
        user_id,
        business_id,
        business_name,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        website_url
      ) VALUES (
        ${createBusinessContactDetailsRequest.userId},
        ${createBusinessContactDetailsRequest.businessId},
        ${createBusinessContactDetailsRequest.businessName},
        ${createBusinessContactDetailsRequest.primaryContactFirstName},
        ${createBusinessContactDetailsRequest.primaryContactLastName},
        ${createBusinessContactDetailsRequest.contactEmail},
        ${createBusinessContactDetailsRequest.contactNumber},
        ${createBusinessContactDetailsRequest.websiteUrl}
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

  override def update(businessId: String, request: UpdateBusinessContactDetailsRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    sql"""
      UPDATE business_contact_details
      SET
        business_name = ${request.businessName},
        primary_contact_first_name = ${request.primaryContactFirstName},
        primary_contact_last_name = ${request.primaryContactLastName},
        contact_email = ${request.contactEmail},
        contact_number = ${request.contactNumber},
        website_url = ${request.websiteUrl},
        updated_at = ${request.updatedAt}
      WHERE business_id = $businessId
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

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteQuery: Update0 =
      sql"""
        DELETE FROM business_contact_details
        WHERE business_id = $businessId
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

object BusinessContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessContactDetailsRepositoryAlgebra[F] =
    new BusinessContactDetailsRepositoryImpl[F](transactor)
}
