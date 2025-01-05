package services.business.address

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.business.address.errors.*
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.business.address.BusinessAddressPartial
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import repositories.business.BusinessAddressRepositoryAlgebra

trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddressPartial]]

  def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[BusinessAddressErrors, DatabaseSuccess]]

  def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class BusinessAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
) extends BusinessAddressServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddressPartial]] =
    businessAddressRepo.findByBusinessId(businessId).flatMap {
      case Some(business) =>
        Concurrent[F].pure(Right(business))
      case None =>
        Concurrent[F].pure(Left(BusinessAddressNotFound))
    }

  override def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAddressRepo.create(businessAddressRequest)

  override def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[BusinessAddressErrors, DatabaseSuccess]] = {

    val updateAddress: F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
      businessAddressRepo.update(businessId, request)

    updateAddress
      .map {
        case Valid(addressId) =>
          Valid(CreateSuccess)
        case addressResult =>
          val errors =
            List(addressResult.toEither.left.getOrElse(Nil))
          BusinessAddressNotFound.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessAddressNotFound.invalidNel)
      }
  }

  override def delete(businessId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    businessAddressRepo.delete(businessId)

}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
  ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}
