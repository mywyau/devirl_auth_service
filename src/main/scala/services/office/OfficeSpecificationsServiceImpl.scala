package services.office

import cats.data.Validated
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Concurrent
import cats.implicits.*
import cats.Monad
import cats.NonEmptyParallel
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.address_details.errors.OfficeAddressErrors
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.specifications.errors.*
import models.office.specifications.requests.CreateOfficeSpecificationsRequest
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeSpecificationsPartial
import repositories.office.OfficeSpecificationsRepositoryAlgebra
import models.database.CreateSuccess

trait OfficeSpecificationsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeSpecificationsErrors, OfficeSpecificationsPartial]]

  def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[OfficeSpecificationsErrors, DatabaseSuccess]]

  def update(officeId: String, officeSpecifications: UpdateOfficeSpecificationsRequest): F[ValidatedNel[OfficeSpecificationsErrors, DatabaseSuccess]]

  def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]]
}

class OfficeSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
  officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]
) extends OfficeSpecificationsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Either[OfficeSpecificationsErrors, OfficeSpecificationsPartial]] =
    officeSpecificationsRepo.findByOfficeId(officeId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeSpecificationsNotFound))
    }

  override def create(createOfficeSpecificationsRequest: CreateOfficeSpecificationsRequest): F[ValidatedNel[OfficeSpecificationsErrors, DatabaseSuccess]] = {

    val specificationsCreation =
      officeSpecificationsRepo.create(createOfficeSpecificationsRequest)

    specificationsCreation
      .map {
        case Validated.Valid(i) =>
          Valid(i)
        case officeSpecificationsResult =>
          val errors =
            List(officeSpecificationsResult.toEither.left.getOrElse(Nil))
          OfficeSpecificationsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(OfficeSpecificationsDatabaseError.invalidNel)
      }
  }

  override def update(officeId: String, request: UpdateOfficeSpecificationsRequest): F[ValidatedNel[OfficeSpecificationsErrors, DatabaseSuccess]] = {

    val updateSpecifications =
      officeSpecificationsRepo.update(officeId, request)

    updateSpecifications
      .map {
        case Valid(specificationsId) =>
          Valid(CreateSuccess)
        case result =>
          val errors =
            List(result.toEither.left.getOrElse(Nil))
          OfficeSpecificationsNotCreated.invalidNel
      }
      .handleErrorWith { e =>
        Concurrent[F].pure(OfficeSpecificationsDatabaseError.invalidNel)
      }
  }

  override def delete(officeId: String): F[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    officeSpecificationsRepo.delete(officeId)
}

object OfficeSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]): OfficeSpecificationsServiceImpl[F] =
    new OfficeSpecificationsServiceImpl[F](officeSpecificationsRepo)
}
