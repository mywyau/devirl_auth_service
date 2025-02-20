package services.business

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.address.BusinessAddressPartial
import models.business.address.CreateBusinessAddressRequest
import models.business.address.UpdateBusinessAddressRequest
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.database.UpdateSuccess
import repositories.business.BusinessAddressRepositoryAlgebra

trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Option[BusinessAddressPartial]]

  def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
) extends BusinessAddressServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Option[BusinessAddressPartial]] =
    businessAddressRepo.findByBusinessId(businessId)

  override def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAddressRepo.create(businessAddressRequest)

  override def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAddressRepo.update(businessId, request)

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAddressRepo.delete(businessId)

}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
  ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}
