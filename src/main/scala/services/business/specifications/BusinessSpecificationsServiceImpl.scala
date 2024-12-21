package services.business.specifications

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.specifications.BusinessSpecifications
import models.business.specifications.errors.*
import models.database.SqlErrors
import repositories.business.BusinessSpecificationsRepositoryAlgebra

trait BusinessSpecificationsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessSpecificationsErrors, BusinessSpecifications]]

  def create(businessSpecifications: BusinessSpecifications): F[cats.data.ValidatedNel[BusinessSpecificationsErrors, Int]]

  def delete(businessId: String): F[ValidatedNel[SqlErrors, Int]]
}


class BusinessSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                       businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
                                                                                     ) extends BusinessSpecificationsServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessSpecificationsErrors, BusinessSpecifications]] = {
    businessSpecificationsRepo.findByBusinessId(businessId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessSpecificationsNotFound))
    }
  }

  override def create(businessSpecifications: BusinessSpecifications): F[ValidatedNel[BusinessSpecificationsErrors, Int]] = {

    val specificationsCreation: F[ValidatedNel[SqlErrors, Int]] =
      businessSpecificationsRepo.createSpecs(businessSpecifications)

    specificationsCreation.map {
      case Validated.Valid(i) =>
        Valid(i)
      case businessSpecificationsResult =>
        val errors =
          List(businessSpecificationsResult.toEither.left.getOrElse(Nil))
        BusinessSpecificationsNotCreated.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(BusinessSpecificationsDatabaseError.invalidNel)
    }
  }

  override def delete(businessId: String): F[ValidatedNel[SqlErrors, Int]] = {
    businessSpecificationsRepo.deleteSpecifications(businessId)
  }
}

object BusinessSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
                                                 ): BusinessSpecificationsServiceImpl[F] =
    new BusinessSpecificationsServiceImpl[F](businessSpecificationsRepo)
}

