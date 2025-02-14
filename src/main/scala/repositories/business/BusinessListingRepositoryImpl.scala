package repositories.business

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import cats.Monad
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import io.circe.syntax.EncoderOps
import io.circe.parser.decode
import java.sql.Timestamp
import java.time.LocalDateTime
import models.business.address.BusinessAddressPartial
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.specifications.BusinessSpecificationsPartial
import models.business_listing.requests.InitiateBusinessListingRequest
import models.business_listing.BusinessListing
import models.database.*
import models.OpeningHours

trait BusinessListingRepositoryAlgebra[F[_]] {

  def findAll(): F[List[BusinessListing]]

  def findByBusinessId(businessId: String): F[Option[BusinessListing]]

  def initiate(request: InitiateBusinessListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

}

class BusinessListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val opening_hoursListMeta: Meta[List[OpeningHours]] =
    Meta[String].imap(jsonStr => decode[List[OpeningHours]](jsonStr).getOrElse(Nil))(_.asJson.noSpaces)

  override def findAll(): F[List[BusinessListing]] = {

    val fetchBusinessAddressDetails =
      sql"""
          SELECT 
             user_id,
             business_id,
             building_name,
             floor_number,
             street,
             city,
             country,
             county,
             postcode,
             latitude,
             longitude
         FROM business_address
      """.query[BusinessAddressPartial].to[List]

    val fetchBusinessContactDetails =
      sql"""
         SELECT 
             user_id,
             business_id,
             primary_contact_first_name,
             primary_contact_last_name,
             contact_email,
             contact_number,
             website_url
         FROM business_contact_details
       """.query[BusinessContactDetailsPartial].to[List]

    val fetchBusinessSpecifications =
      sql"""
         SELECT 
           user_id,
           business_id,
           business_name,
           description,
           opening_hours
         FROM business_specifications
      """.query[BusinessSpecificationsPartial].to[List]

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
          } yield BusinessListing(address.userId, address.businessId, address, contact, specs)
        }
      }
    ).transact(transactor)
  }

  override def findByBusinessId(businessId: String): F[Option[BusinessListing]] = {
    val fetchBusinessAddressDetails =
      sql"""
        SELECT 
             user_id,
             business_id,
             building_name,
             floor_number,
             street,
             city,
             country,
             county,
             postcode,
             latitude,
             longitude
        FROM business_address WHERE business_id = $businessId
      """.query[BusinessAddressPartial].option

    val fetchBusinessContactDetails =
      sql"""
        SELECT
          user_id,
          business_id,
          primary_contact_first_name,
          primary_contact_last_name,
          contact_email,
          contact_number,
          website_url
        FROM business_contact_details WHERE business_id = $businessId
      """.query[BusinessContactDetailsPartial].option

    val fetchBusinessSpecifications =
      sql"""
        SELECT 
           user_id,
           business_id,
           business_name,
           description,
           opening_hours
        FROM business_specifications WHERE business_id = $businessId
      """.query[BusinessSpecificationsPartial].option
    (
      for {
        addressDetails <- fetchBusinessAddressDetails
        contactDetails <- fetchBusinessContactDetails
        specifications <- fetchBusinessSpecifications
      } yield (addressDetails, contactDetails, specifications) match {
        case (Some(address), Some(contact), Some(specs)) =>
          Some(BusinessListing(address.userId, businessId, address, contact, specs))
        case _ =>
          None
      }
    ).transact(transactor)
  }

  override def initiate(request: InitiateBusinessListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
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
      case Right(affectedRows) if affectedRows == 3 =>
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
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
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
      case Right(affectedRows) if affectedRows == 3 =>
        DeleteSuccess.validNel
      case Right(affectedRows) if affectedRows < 3 =>
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

  override def deleteByUserId(userId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
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

object BusinessListingRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): BusinessListingRepositoryAlgebra[F] =
    new BusinessListingRepositoryImpl[F](transactor)
}
