package services.business.address

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.address.BusinessAddress
import models.business.address.errors.*
import models.business.address.requests.CreateBusinessAddressRequest
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra


trait BusinessAddressServiceAlgebra[F[_]] {

  def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]]

  def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]]

  def deleteAddress(businessId: String): F[ValidatedNel[SqlErrors, Int]]
}

class BusinessAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
                                                                              ) extends BusinessAddressServiceAlgebra[F] {

  override def getByBusinessId(businessId: String): F[Either[BusinessAddressErrors, BusinessAddress]] = {
    businessAddressRepo.findByBusinessId(businessId).flatMap {
      case Some(business) =>
        Concurrent[F].pure(Right(business))
      case None =>
        Concurrent[F].pure(Left(BusinessAddressNotFound))
    }
  }

  override def createAddress(businessAddressRequest: CreateBusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]] = {
    businessAddressRepo.createBusinessAddress(businessAddressRequest)
  }

  override def deleteAddress(businessId: String): F[ValidatedNel[SqlErrors, Int]] = {
    businessAddressRepo.deleteBusinessAddress(businessId)
  }

}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
                                                 ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}

