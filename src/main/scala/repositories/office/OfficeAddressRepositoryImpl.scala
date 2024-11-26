package repositories.office

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.office.office_address.OfficeAddress

import java.sql.Timestamp
import java.time.LocalDateTime

trait OfficeAddressRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[OfficeAddress]]
  
  def createOfficeAddress(user: OfficeAddress): F[Int]
}

class OfficeAddressRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends OfficeAddressRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)
  
  override def findByBusinessId(businessId: String): F[Option[OfficeAddress]] = {
    val findQuery: F[Option[OfficeAddress]] =
      sql"SELECT * FROM office_address WHERE business_id = $businessId"
        .query[OfficeAddress]
        .option
        .transact(transactor)
    findQuery
  }

  override def createOfficeAddress(officeAddress: OfficeAddress): F[Int] = {
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
  }

}


object OfficeAddressRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): OfficeAddressRepositoryImpl[F] =
    new OfficeAddressRepositoryImpl[F](transactor)
}
