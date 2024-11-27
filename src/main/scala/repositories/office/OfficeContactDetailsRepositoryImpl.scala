package repositories.office

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.office.office_contact_details.OfficeContactDetails

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeContactDetailsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[OfficeContactDetails]]

  def createContactDetails(officeContactDetails: OfficeContactDetails): F[Int]
}

class OfficeContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[OfficeContactDetails]] = {
    val findQuery: F[Option[OfficeContactDetails]] =
      sql"SELECT * FROM office_contact_details WHERE business_id = $businessId"
        .query[OfficeContactDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def createContactDetails(officeContactDetails: OfficeContactDetails): F[Int] = {
    sql"""
      INSERT INTO office_contact_details (
        business_id,
        office_id,
        primary_contact_first_name,
        primary_contact_last_name,
        contact_email,
        contact_number,
        created_at,
        updated_at
      ) VALUES (
        ${officeContactDetails.businessId},
        ${officeContactDetails.officeId},
        ${officeContactDetails.primaryContactFirstName},
        ${officeContactDetails.primaryContactLastName},
        ${officeContactDetails.contactEmail},
        ${officeContactDetails.contactNumber},
        ${officeContactDetails.createdAt},
        ${officeContactDetails.updatedAt}
      )
    """.update
      .run
      .transact(transactor)
  }

}


object OfficeContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeContactDetailsRepositoryImpl[F] =
    new OfficeContactDetailsRepositoryImpl[F](transactor)
}
