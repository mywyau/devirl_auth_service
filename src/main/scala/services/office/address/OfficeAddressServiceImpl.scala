package services.office.address

import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.database.*
import models.office.office_address.OfficeAddress
import models.office.office_address.errors.*
import repositories.office.OfficeAddressRepositoryAlgebra
import cats.data.Validated
import cats.data.ValidatedNel
import cats.data.Validated.{Invalid, Valid}


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

  override def createOfficeAddress(officeAddress: OfficeAddress): F[ValidatedNel[OfficeAddressErrors, Int]] = {

    val addressCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeAddressRepo.createOfficeAddress(officeAddress)

    addressCreation.map {
      case Validated.Valid(addressId) =>
        Valid(1)
      case addressResult =>
        val errors =
          List(addressResult.toEither.left.getOrElse(Nil))
        OfficeAddressNotCreated.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(AddressDatabaseError.invalidNel)
    }
  }
}

object OfficeAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeAddressRepo: OfficeAddressRepositoryAlgebra[F]
                                                 ): OfficeAddressServiceImpl[F] =
    new OfficeAddressServiceImpl[F](officeAddressRepo)
}

