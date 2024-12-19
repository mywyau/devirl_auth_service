package repositories.business

import cats.Monad
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.postgres.implicits.*
import doobie.util.meta.Meta
import io.circe.syntax.*
import models.business.specifications.BusinessSpecifications
import models.database.*

import java.sql.Timestamp
import java.time.LocalDateTime

trait BusinessSpecificationsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessSpecifications]]

  def createSpecs(businessSpecifications: BusinessSpecifications): F[ValidatedNel[SqlErrors, Int]]
}

class BusinessSpecificationsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessSpecificationsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessSpecifications]] = {
    val findQuery: F[Option[BusinessSpecifications]] =
      sql"SELECT * FROM business_specs WHERE business_id = $businessId"
        .query[BusinessSpecifications]
        .option
        .transact(transactor)
    findQuery
  }

  override def createSpecs(businessSpecs: BusinessSpecifications): F[ValidatedNel[SqlErrors, Int]] = {
    sql"""
      INSERT INTO business_specs (
        user_id,
        business_id,
        business_name,
        description,
        created_at,
        updated_at
      ) VALUES (
        ${businessSpecs.userId},
        ${businessSpecs.businessId},
        ${businessSpecs.businessName},
        ${businessSpecs.description},
        ${businessSpecs.createdAt},
        ${businessSpecs.updatedAt}
      )
    """
      .update
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


object BusinessSpecificationsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessSpecificationsRepositoryAlgebra[F] =
    new BusinessSpecificationsRepositoryImpl[F](transactor)
}
