package services.office.office_address

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.office.office_address.errors.*
import models.office.office_address.OfficeAddress
import models.office.office_address.OfficeAddress
import models.office.office_address.errors.OfficeAddressErrors
import repositories.office.OfficeAddressRepositoryAlgebra


class OfficeAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeAddressRepo: OfficeAddressRepositoryAlgebra[F]
                                                                            ) extends OfficeAddressServiceAlgebra[F] {

  override def getAddressByBusinessId(userId: String): F[Either[OfficeAddressErrors, OfficeAddress]] = {
    officeAddressRepo.findByBusinessId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeAddressNotFound))
    }
  }

  override def createOfficeAddress(officeAddress: OfficeAddress): F[Int] = {
    officeAddressRepo.createOfficeAddress(officeAddress)
  }
}

object OfficeAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeAddressRepo: OfficeAddressRepositoryAlgebra[F]
                                                 ): OfficeAddressServiceImpl[F] =
    new OfficeAddressServiceImpl[F](officeAddressRepo)
}

