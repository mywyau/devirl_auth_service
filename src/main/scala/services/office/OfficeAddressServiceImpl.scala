package services.office

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.*
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.UpdateOfficeAddressRequest
import org.typelevel.log4cats.Logger
import repositories.office.OfficeAddressRepositoryAlgebra

trait OfficeAddressServiceAlgebra[F[_]] {

  def findByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, OfficeAddressPartial]]

  def create(officeAddress: CreateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, officeAddress: UpdateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad : Logger](
  officeAddressRepository: OfficeAddressRepositoryAlgebra[F]
) extends OfficeAddressServiceAlgebra[F] {

  override def findByOfficeId(officeId: String): F[ValidatedNel[DatabaseErrors, OfficeAddressPartial]] =
    officeAddressRepository.findByOfficeId(officeId)

  override def create(request: CreateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeAddressRepository.create(request)

  override def update(officeId: String, request: UpdateOfficeAddressRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeAddressRepository.update(officeId, request)

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeAddressRepository.delete(officeId)

}

object OfficeAddressService {

  def apply[F[_] : Concurrent : NonEmptyParallel : Monad : Logger](officeAddressRepository: OfficeAddressRepositoryAlgebra[F]): OfficeAddressServiceImpl[F] =
    new OfficeAddressServiceImpl[F](officeAddressRepository)
}
