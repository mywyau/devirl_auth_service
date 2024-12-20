package services.business.address

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.address_details.errors.*
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra


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

  override def createAddress(businessAddressRequest: BusinessAddressRequest): F[ValidatedNel[SqlErrors, Int]] = {
    businessAddressRepo.createBusinessAddress(businessAddressRequest)
  }
}

object BusinessAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessAddressRepo: BusinessAddressRepositoryAlgebra[F]
                                                 ): BusinessAddressServiceImpl[F] =
    new BusinessAddressServiceImpl[F](businessAddressRepo)
}

