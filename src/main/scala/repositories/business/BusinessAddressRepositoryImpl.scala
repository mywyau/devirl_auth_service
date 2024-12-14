package repositories.business

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.business.business_address.service.BusinessAddress
import models.database.*

import java.sql.Timestamp
import java.time.LocalDateTime


trait BusinessAddressRepositoryAlgebra[F[_]] {

  def findByUserId(userId: String): F[Option[BusinessAddress]]

  def createBusinessAddress(businessAddress: BusinessAddress): F[ValidatedNel[SqlErrors, Int]]
}

class BusinessAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByUserId(userId: String): F[Option[BusinessAddress]] = {
    val findQuery: F[Option[BusinessAddress]] =
      sql"SELECT * FROM business_address WHERE user_id = $userId"
        .query[BusinessAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def createBusinessAddress(businessAddress: BusinessAddress): F[ValidatedNel[SqlErrors, Int]] = {
    sql"""
      INSERT INTO business_address (
        user_id,
        business_id,
        business_name,
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
      )
      VALUES (
        ${businessAddress.userId},
        ${businessAddress.businessId},
        ${businessAddress.businessName},
        ${businessAddress.buildingName},
        ${businessAddress.floorNumber},
        ${businessAddress.street},
        ${businessAddress.city},
        ${businessAddress.country},
        ${businessAddress.county},
        ${businessAddress.postcode},
        ${businessAddress.latitude},
        ${businessAddress.longitude},
        ${businessAddress.createdAt},
        ${businessAddress.updatedAt}
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
}


object BusinessAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessAddressRepositoryImpl[F] =
    new BusinessAddressRepositoryImpl[F](transactor)
}
