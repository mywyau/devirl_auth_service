package repositories

import cats.effect.Concurrent
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.meta.Meta
import models.users.Business

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

trait BusinessRepositoryAlgebra[F[_]] {

  def findBusinessById(businessId: String): F[Option[Business]]

  def findBusinessByName(businessName: String): F[Option[Business]]

  def getAllBusiness: F[List[Business]]

  def setBusiness(business: Business): F[Int]

  def updateBusiness(businessId: String, updatedBusiness: Business): F[Int]

  def deleteBusiness(businessId: String): F[Int]
}

class BusinessRepository[F[_] : Concurrent](transactor: Transactor[F]) extends BusinessRepositoryAlgebra[F] {

  // Meta instance to map between LocalDateTime and Timestamp
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  // Meta instance to map between LocalDate
  implicit val localDateMeta: Meta[LocalDate] =
    Meta[Date].imap(_.toLocalDate)(Date.valueOf)

  //  implicit val businessStatusMeta: Meta[BusinessStatus] = Meta[String].imap(BusinessStatus.fromString)(_.toString)

  def findBusinessById(businessId: String): F[Option[Business]] = {
    sql"SELECT * FROM business WHERE business_id = $businessId"
      .query[Business]
      .option
      .transact(transactor)
  }

  def findBusinessByName(businessName: String): F[Option[Business]] = {
    sql"SELECT * FROM business WHERE business_name = $businessName"
      .query[Business]
      .option
      .transact(transactor)
  }


  def getAllBusiness: F[List[Business]] = {
    sql"SELECT * FROM business"
      .query[Business]
      .to[List]
      .transact(transactor)
  }

  def setBusiness(business: Business): F[Int] = {
    sql"""
      INSERT INTO business (business_id, business_name, contact_number, contact_email, created_at)
      VALUES (${business.business_id}, ${business.business_name}, ${business.contact_number}, ${business.contact_email}, ${business.created_at})
    """.update
      .run
      .transact(transactor)
  }


  def updateBusiness(businessId: String, updatedBusiness: Business): F[Int] = {
    sql"""
      UPDATE business
      SET business_name = ${updatedBusiness.business_name},
          contact_number = ${updatedBusiness.contact_number},
          contact_email = ${updatedBusiness.contact_email},
          created_at = ${updatedBusiness.created_at},
      WHERE business_id = $businessId
  """.update
      .run
      .transact(transactor)
  }

  def deleteBusiness(businessId: String): F[Int] = {
    sql"""
      DELETE FROM business WHERE business_id = $businessId
    """.update
      .run
      .transact(transactor)
  }

}
