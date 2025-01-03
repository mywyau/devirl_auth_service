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
import models.business.address.BusinessAddress
import models.business.business_listing.requests.InitiateBusinessListingRequest
import models.business.business_listing.BusinessListing
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.BusinessSpecifications
import models.database.*

trait BusinessListingRepositoryAlgebra[F[_]] {

  def findAll(): F[List[BusinessListing]]

  def findByBusinessId(businessId: String): F[Option[BusinessListing]]

  def initiate(request: InitiateBusinessListingRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]

  def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, Int]]

}

class BusinessListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findAll(): F[List[BusinessListing]] = {
    val fetchBusinessAddressDetails =
      sql"""
        SELECT * FROM business_address
      """.query[BusinessAddress].to[List]

    val fetchBusinessContactDetails =
      sql"""
        SELECT * FROM business_contact_details
      """.query[BusinessContactDetails].to[List]

    val fetchBusinessSpecifications =
      sql"""
        SELECT * FROM business_specifications
      """.query[BusinessSpecifications].to[List]

    (
      for {
        addressDetails <- fetchBusinessAddressDetails
        contactDetails <- fetchBusinessContactDetails
        specifications <- fetchBusinessSpecifications
      } yield {

        // Map for quick lookup by businessId
        val contactMap = contactDetails.map(c => c.businessId -> c).toMap
        val specsMap = specifications.map(s => s.businessId -> s).toMap

        addressDetails.flatMap { address =>
          for {
            contact <- contactMap.get(address.businessId)
            specs <- specsMap.get(address.businessId)
          } yield BusinessListing(address.businessId, address, contact, specs)
        }
      }
    ).transact(transactor)
  }

  override def findByBusinessId(businessId: String): F[Option[BusinessListing]] = {
    val fetchBusinessAddressDetails =
      sql"""
        SELECT * FROM business_address WHERE business_id = $businessId
      """.query[BusinessAddress].option

    val fetchBusinessContactDetails =
      sql"""
        SELECT * FROM business_contact_details WHERE business_id = $businessId
      """.query[BusinessContactDetails].option

    val fetchBusinessSpecifications =
      sql"""
        SELECT * FROM business_specifications WHERE business_id = $businessId
      """.query[BusinessSpecifications].option
    (
      for {
        addressDetails <- fetchBusinessAddressDetails
        contactDetails <- fetchBusinessContactDetails
        specifications <- fetchBusinessSpecifications
      } yield (addressDetails, contactDetails, specifications) match {
        case (Some(address), Some(contact), Some(specs)) =>
          Some(BusinessListing(businessId, address, contact, specs))
        case _ =>
          None
      }
    ).transact(transactor)
  }

  override def initiate(request: InitiateBusinessListingRequest): F[ValidatedNel[DatabaseErrors, Int]] = {
    val insertBusinessAddress =
      sql"""
        INSERT INTO business_address (
          user_id,
          business_id
        ) VALUES (
          ${request.userId},
          ${request.businessId}
        )
      """

    val insertBusinessContactDetails =
      sql"""
        INSERT INTO business_contact_details (
          user_id,
          business_id
        ) VALUES (
          ${request.userId},
          ${request.businessId}
        )
      """

    val insertBusinessSpecifications =
      sql"""
        INSERT INTO business_specifications (
          user_id,
          business_id,
          business_name,
          description
        ) VALUES (
          ${request.userId},
          ${request.businessId},
          ${request.businessName},
          ${request.description}
        )
      """

    (for {
      rowsBusinessAddress <- insertBusinessAddress.update.run
      rowsBusinessContactDetails <- insertBusinessContactDetails.update.run
      rowsBusinessSpecifications <- insertBusinessSpecifications.update.run
    } yield rowsBusinessAddress + rowsBusinessContactDetails + rowsBusinessSpecifications).transact(transactor).attempt.map {
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
        UnknownError(e.getMessage).invalidNel
    }
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteAddressQuery =
      sql"""
           DELETE FROM business_address
           WHERE business_id = $businessId
         """.update.run

    val deleteContactDetailsQuery =
      sql"""
           DELETE FROM business_contact_details
           WHERE business_id = $businessId
         """.update.run

    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM business_specifications
           WHERE business_id = $businessId
         """.update.run

    val combinedQuery = for {
      addressRows <- deleteAddressQuery
      contactRows <- deleteContactDetailsQuery
      specsRows <- deleteSpecificationsQuery
    } yield addressRows + contactRows + specsRows

    combinedQuery.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows == 3)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex) =>
        DeleteError.invalidNel
    }
  }

  override def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, Int]] = {
    val deleteAddressQuery =
      sql"""
           DELETE FROM business_address
           WHERE user_id = $userId
         """.update.run

    val deleteContactDetailsQuery =
      sql"""
           DELETE FROM business_contact_details
           WHERE user_id = $userId
         """.update.run

    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM business_specifications
           WHERE user_id = $userId
         """.update.run

    val combinedQuery = for {
      addressRows <- deleteAddressQuery
      contactRows <- deleteContactDetailsQuery
      specsRows <- deleteSpecificationsQuery
    } yield addressRows + contactRows + specsRows

    combinedQuery.transact(transactor).attempt.map {
      case Right(affectedRows) =>
        if (affectedRows == 3)
          affectedRows.validNel
        else
          NotFoundError.invalidNel
      case Left(ex) =>
        DeleteError.invalidNel
    }
  }

}

object BusinessListingRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessListingRepositoryAlgebra[F] =
    new BusinessListingRepositoryImpl[F](transactor)
}
