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
import models.database.*
import models.office.address_details.OfficeAddress
import models.office.adts.OfficeType
import models.office.contact_details.OfficeContactDetails
import models.office.office_listing.OfficeListing
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.specifications.OfficeSpecifications

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeListingRepositoryAlgebra[F[_]] {

  def findAll(): F[List[OfficeListing]]

  def findByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[ValidatedNel[SqlErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]

}

class OfficeListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findAll(): F[List[OfficeListing]] = {
    val fetchOfficeAddressDetails =
      sql"""
        SELECT * FROM office_address
      """.query[OfficeAddress].to[List]

    val fetchOfficeContactDetails =
      sql"""
        SELECT * FROM office_contact_details
      """.query[OfficeContactDetails].to[List]

    val fetchOfficeSpecifications =
      sql"""
        SELECT * FROM office_specs
      """.query[OfficeSpecifications].to[List]

    (
      for {
        addressDetails <- fetchOfficeAddressDetails
        contactDetails <- fetchOfficeContactDetails
        specifications <- fetchOfficeSpecifications
      } yield {
        // Map for quick lookup by officeId
        val contactMap = contactDetails.map(c => c.officeId -> c).toMap
        val specsMap = specifications.map(s => s.officeId -> s).toMap

        addressDetails.flatMap { address =>
          for {
            contact <- contactMap.get(address.officeId)
            specs <- specsMap.get(address.officeId)
          } yield {
            OfficeListing(address.officeId, address, contact, specs)
          }
        }
      }
      ).transact(transactor)
  }

  override def findByOfficeId(officeId: String): F[Option[OfficeListing]] = {
    val fetchOfficeAddressDetails =
      sql"""
        SELECT * FROM office_address WHERE office_id = $officeId
      """.query[OfficeAddress]
        .option

    val fetchOfficeContactDetails =
      sql"""
        SELECT * FROM office_contact_details WHERE office_id = $officeId
      """.query[OfficeContactDetails]
        .option

    val fetchOfficeSpecifications = {
      sql"""
        SELECT * FROM office_specs WHERE office_id = $officeId
      """.query[OfficeSpecifications]
        .option
    }
    (
      for {
        addressDetails <- fetchOfficeAddressDetails
        contactDetails <- fetchOfficeContactDetails
        specifications <- fetchOfficeSpecifications
      } yield {
        (addressDetails, contactDetails, specifications) match {
          case (Some(address), Some(contact), Some(specs)) =>
            Some(OfficeListing(officeId, address, contact, specs))
          case _ =>
            None
        }
      }
      ).transact(transactor)
  }


  override def initiate(request: InitiateOfficeListingRequest): F[ValidatedNel[SqlErrors, Int]] = {
    val insertOfficeListing =
      sql"""
        INSERT INTO office_address (
          business_id,
          office_id
        ) VALUES (
          ${request.businessId},
          ${request.officeId}
        )
      """

    val insertOfficeDetails =
      sql"""
        INSERT INTO office_contact_details (
          office_id,
          business_id
        ) VALUES (
          ${request.officeId},
          ${request.businessId}
        )
      """

    val insertBusinessDetails =
      sql"""
        INSERT INTO office_specs (
          business_id,
          office_id
        ) VALUES (
          ${request.businessId},
          ${request.officeId}
        )
      """

    (for {
      rowsOfficeListing <- insertOfficeListing.update.run
      rowsOfficeDetails <- insertOfficeDetails.update.run
      rowsBusinessDetails <- insertBusinessDetails.update.run
    } yield rowsOfficeListing + rowsOfficeDetails + rowsBusinessDetails)
      .transact(transactor)
      .attempt
      .map {
        case Right(totalRows) =>
          if (totalRows == 3) {
            totalRows.validNel
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

  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = ???
}


object OfficeListingRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeListingRepositoryAlgebra[F] =
    new OfficeListingRepositoryImpl[F](transactor)
}
