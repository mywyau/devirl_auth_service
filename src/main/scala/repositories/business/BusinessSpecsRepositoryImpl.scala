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
import models.business.business_specs.BusinessSpecs
import models.database.*

import java.sql.Timestamp
import java.time.LocalDateTime

trait BusinessSpecsRepositoryAlgebra[F[_]] {

  def findByBusinessId(businessId: String): F[Option[BusinessSpecs]]

  def createSpecs(user: BusinessSpecs): F[ValidatedNel[SqlErrors, Int]]
}

class BusinessSpecsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends BusinessSpecsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  override def findByBusinessId(businessId: String): F[Option[BusinessSpecs]] = {
    val findQuery: F[Option[BusinessSpecs]] =
      sql"SELECT * FROM business_specs WHERE business_id = $businessId"
        .query[BusinessSpecs]
        .option
        .transact(transactor)
    findQuery
  }

  override def createSpecs(businessSpecs: BusinessSpecs): F[ValidatedNel[SqlErrors, Int]] = {
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


object BusinessSpecsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): BusinessSpecsRepositoryAlgebra[F] =
    new BusinessSpecsRepositoryImpl[F](transactor)
}
