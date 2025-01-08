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
import java.sql.Timestamp
import java.time.LocalDateTime
import models.database.*
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import models.office.adts.OfficeType
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.office_listing.requests.InitiateOfficeListingRequest
import models.office.office_listing.OfficeListing
import models.office.specifications.OfficeSpecificationsPartial

trait OfficeListingRepositoryAlgebra[F[_]] {

  def findAll(businessId: String): F[List[OfficeListing]]

  def findByOfficeId(officeId: String): F[Option[OfficeListing]]

  def initiate(request: InitiateOfficeListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def deleteByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

}

class OfficeListingRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeListingRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val officeTypeMeta: Meta[OfficeType] = Meta[String].imap(OfficeType.fromString)(_.toString)

  override def findAll(businessId: String): F[List[OfficeListing]] = {
    val fetchAllOfficeDetails =
      sql"""
      SELECT 
          oa.business_id AS oa_business_id,
          oa.office_id AS oa_office_id,
          oa.building_name AS oa_building_name,
          oa.floor_number AS oa_floor_number,
          oa.street AS oa_street,
          oa.city AS oa_city,
          oa.country AS oa_country,
          oa.county AS oa_county,
          oa.postcode AS oa_postcode,
          oa.latitude AS oa_latitude,
          oa.longitude AS oa_longitude,

          ocd.business_id AS ocd_business_id,
          ocd.office_id AS ocd_office_id,
          ocd.primary_contact_first_name AS ocd_primary_contact_first_name,
          ocd.primary_contact_last_name AS ocd_primary_contact_last_name,
          ocd.contact_email AS ocd_contact_email,
          ocd.contact_number AS ocd_contact_number,

          os.business_id AS os_business_id,
          os.office_id AS os_office_id,
          os.office_name AS os_office_name,
          os.description AS os_description,
          os.office_type AS os_office_type,
          os.number_of_floors AS os_number_of_floors,
          os.total_desks AS os_total_desks,
          os.capacity AS os_capacity,
          os.amenities AS os_amenities,
          os.availability AS os_availability,
          os.rules AS os_rules
      FROM office_address oa
      LEFT JOIN office_contact_details ocd ON oa.office_id = ocd.office_id
      LEFT JOIN office_specifications os ON oa.office_id = os.office_id
      WHERE oa.business_id = $businessId
    """.query[(OfficeAddressPartial, OfficeContactDetailsPartial, OfficeSpecificationsPartial)]
        .to[List]

    fetchAllOfficeDetails
      .map { results =>
        results.map { case (address, contact, specs) =>
          OfficeListing(
            officeId = address.officeId,
            addressDetails = address,
            contactDetails = contact,
            specifications = specs
          )
        }
      }
      .transact(transactor)
  }

  override def findByOfficeId(officeId: String): F[Option[OfficeListing]] = {
    val fetchOfficeDetails =
      sql"""
        SELECT 
          oa.business_id AS oa_business_id,
          oa.office_id AS oa_office_id,
          oa.building_name AS oa_building_name,
          oa.floor_number AS oa_floor_number,
          oa.street AS oa_street,
          oa.city AS oa_city,
          oa.country AS oa_country,
          oa.county AS oa_county,
          oa.postcode AS oa_postcode,
          oa.latitude AS oa_latitude,
          oa.longitude AS oa_longitude,

          ocd.business_id AS ocd_business_id,
          ocd.office_id AS ocd_office_id,
          ocd.primary_contact_first_name AS ocd_primary_contact_first_name,
          ocd.primary_contact_last_name AS ocd_primary_contact_last_name,
          ocd.contact_email AS ocd_contact_email,
          ocd.contact_number AS ocd_contact_number,

          os.business_id AS os_business_id,
          os.office_id AS os_office_id,
          os.office_name AS os_office_name,
          os.description AS os_description,
          os.office_type AS os_office_type,
          os.number_of_floors AS os_number_of_floors,
          os.total_desks AS os_total_desks,
          os.capacity AS os_capacity,
          os.amenities AS os_amenities,
          os.availability AS os_availability,
          os.rules AS os_rules
      FROM office_address oa
      LEFT JOIN office_contact_details ocd ON oa.office_id = ocd.office_id
      LEFT JOIN office_specifications os ON oa.office_id = os.office_id
      WHERE oa.office_id = $officeId
    """.query[(OfficeAddressPartial, OfficeContactDetailsPartial, OfficeSpecificationsPartial)].option

    fetchOfficeDetails
      .map {
        case Some((address, contact, specs)) =>
          Some(
            OfficeListing(
              officeId = address.officeId,
              addressDetails = address,
              contactDetails = contact,
              specifications = specs
            )
          )
        case None =>
          None
      }
      .transact(transactor)
  }

  override def initiate(request: InitiateOfficeListingRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
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
        INSERT INTO office_specifications (
          business_id,
          office_id,
          office_name,
          description
        ) VALUES (
          ${request.businessId},
          ${request.officeId},
          ${request.officeName},
          ${request.description}
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

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteAddressQuery =
      sql"""
           DELETE FROM office_address
           WHERE office_id = $officeId
         """.update.run

    val deleteContactDetailsQuery =
      sql"""
           DELETE FROM office_contact_details
           WHERE office_id = $officeId
         """.update.run

    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM office_specifications
           WHERE office_id = $officeId
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

  override def deleteByBusinessId(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = {
    val deleteAddressQuery =
      sql"""
           DELETE FROM office_address
           WHERE business_id = $businessId
         """.update.run

    val deleteContactDetailsQuery =
      sql"""
           DELETE FROM office_contact_details
           WHERE business_id = $businessId
         """.update.run

    val deleteSpecificationsQuery =
      sql"""
           DELETE FROM office_specifications
           WHERE business_id = $businessId
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

object OfficeListingRepository {
  def apply[F[_] : Concurrent : Monad](transactor: Transactor[F]): OfficeListingRepositoryAlgebra[F] =
    new OfficeListingRepositoryImpl[F](transactor)
}
