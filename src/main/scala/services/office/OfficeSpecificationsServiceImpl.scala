package services.office

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.address_details.UpdateOfficeAddressRequest
import models.office.specifications.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecificationsPartial
import models.office.specifications.UpdateOfficeSpecificationsRequest
import repositories.office.OfficeSpecificationsRepositoryAlgebra

trait OfficeSpecificationsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Option[OfficeSpecificationsPartial]]

  def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, officeSpecifications: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]
) extends OfficeSpecificationsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Option[OfficeSpecificationsPartial]] =
    officeSpecificationsRepo.findByOfficeId(officeId)

  override def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeSpecificationsRepo.create(createOfficeSpecificationsRequest)

  override def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeSpecificationsRepo.update(officeId, request)

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeSpecificationsRepo.delete(officeId)
}

object OfficeSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]): OfficeSpecificationsServiceImpl[F] =
    new OfficeSpecificationsServiceImpl[F](officeSpecificationsRepo)
}
