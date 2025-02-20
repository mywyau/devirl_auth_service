package services.office

import cats.Monad
import cats.NonEmptyParallel
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.contact_details.UpdateOfficeContactDetailsRequest
import repositories.office.OfficeContactDetailsRepositoryAlgebra

trait OfficeContactDetailsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Option[OfficeContactDetailsPartial]]

  def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def update(officeId: String, officeAddress: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
) extends OfficeContactDetailsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Option[OfficeContactDetailsPartial]] =
    officeContactDetailsRepo.findByOfficeId(officeId)

  override def create(request: CreateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeContactDetailsRepo.create(request)

  override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeContactDetailsRepo.update(officeId, request)

  override def delete(officeId: String) =
    officeContactDetailsRepo.delete(officeId)
}

object OfficeContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
    officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
  ): OfficeContactDetailsServiceImpl[F] =
    new OfficeContactDetailsServiceImpl[F](officeContactDetailsRepo)
}
