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
import models.business.address.BusinessAddress
import models.database.DatabaseErrors
import repositories.business.BusinessAddressRepositoryAlgebra

trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]]

  def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[BusinessAddressErrors, Int]]

  def deleteAddress(businessId: String): F[ValidatedNel[DatabaseErrors, Int]]
}

class BusinessAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
) extends BusinessAddressServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]] =
    businessAddressRepo.findByBusinessId(businessId).flatMap {
      case Some(business) =>
        Concurrent[F].pure(Right(business))
      case None =>
        Concurrent[F].pure(Left(BusinessAddressNotFound))
    }

  override def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[DatabaseErrors, Int]] =
    businessAddressRepo.createBusinessAddress(businessAddressRequest)

  override def update(businessId: String, request: UpdateBusinessAddressRequest): F[ValidatedNel[BusinessAddressErrors, Int]] = {

    val updateAddress: F[ValidatedNel[DatabaseErrors, Int]] =
      businessAddressRepo.update(businessId, request)

    updateAddress
      .map {
        case Valid(addressId) =>
          Valid(1)
        case addressResult =>
          val errors =
            List(addressResult.toEither.left.getOrElse(Nil))
          BusinessAddressNotFound.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(BusinessAddressNotFound.invalidNel)
      }
  }

  override def deleteAddress(businessId: String): F[ValidatedNel[DatabaseErrors, Int]] =
    businessAddressRepo.deleteBusinessAddress(businessId)

}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
  ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}
