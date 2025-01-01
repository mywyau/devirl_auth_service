package services.office.address

import cats.data.Validated.Valid
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.database.*
import models.office.address_details.OfficeAddress
import models.office.address_details.errors.*
import models.office.address_details.requests.{CreateOfficeAddressRequest, UpdateOfficeAddressRequest}
import repositories.office.OfficeAddressRepositoryAlgebra


trait OfficeAddressServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeAddressErrors, OfficeAddress]]

  def create(officeAddress: CreateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, Int]]

  def update(officeId: String, officeAddress: UpdateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]
}


class OfficeAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                              officeAddressRepo: OfficeAddressRepositoryAlgebra[F]
                                                                            ) extends OfficeAddressServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Either[OfficeAddressErrors, OfficeAddress]] = {
    officeAddressRepo.findByOfficeId(officeId).flatMap {
      case Some(office) =>
        Concurrent[F].pure(Right(office))
      case None =>
        Concurrent[F].pure(Left(OfficeAddressNotFound))
    }
  }

  override def create(officeAddressRequest: CreateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, Int]] = {

    val addressCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeAddressRepo.create(officeAddressRequest)

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

  override def update(officeId: String, request: UpdateOfficeAddressRequest): F[ValidatedNel[OfficeAddressErrors, Int]] = {

    val updateAddress: F[ValidatedNel[SqlErrors, Int]] =
      officeAddressRepo.update(officeId, request)

    updateAddress.map {
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

  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    officeAddressRepo.delete(officeId)
  }

}

object OfficeAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeAddressRepo: OfficeAddressRepositoryAlgebra[F]
                                                 ): OfficeAddressServiceImpl[F] =
    new OfficeAddressServiceImpl[F](officeAddressRepo)
}

