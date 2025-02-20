package services.business

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.specifications.CreateBusinessSpecificationsRequest
import models.business.specifications.UpdateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecificationsPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.database.UpdateSuccess
import repositories.business.BusinessSpecificationsRepositoryAlgebra

trait BusinessSpecificationsServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Option[BusinessSpecificationsPartial]]

  def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): F[cats.data.ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
) extends BusinessSpecificationsServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Option[BusinessSpecificationsPartial]] =
    businessSpecificationsRepo.findByBusinessId(businessId)

  override def create(createBusinessSpecificationsRequest: CreateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessSpecificationsRepo.create(createBusinessSpecificationsRequest)

  override def update(businessId: String, request: UpdateBusinessSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessSpecificationsRepo.update(businessId, request)

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessSpecificationsRepo.delete(businessId)
}

object BusinessSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessSpecificationsRepo: BusinessSpecificationsRepositoryAlgebra[F]
  ): BusinessSpecificationsServiceImpl[F] =
    new BusinessSpecificationsServiceImpl[F](businessSpecificationsRepo)
}
