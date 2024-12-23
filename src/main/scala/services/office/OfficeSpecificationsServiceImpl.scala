package services.office

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.office.specifications.OfficeSpecifications
import models.office.specifications.errors.*
import models.database.SqlErrors
import repositories.office.OfficeSpecificationsRepositoryAlgebra

trait OfficeSpecificationsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeSpecificationsErrors, OfficeSpecifications]]

  def create(officeSpecifications: OfficeSpecifications): F[cats.data.ValidatedNel[OfficeSpecificationsErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]
}


class OfficeSpecificationsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                       officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]
                                                                                     ) extends OfficeSpecificationsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Either[OfficeSpecificationsErrors, OfficeSpecifications]] = {
    officeSpecificationsRepo.findByOfficeId(officeId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeSpecificationsNotFound))
    }
  }

  override def create(officeSpecifications: OfficeSpecifications): F[ValidatedNel[OfficeSpecificationsErrors, Int]] = {

    val specificationsCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeSpecificationsRepo.createSpecs(officeSpecifications)

    specificationsCreation.map {
      case Validated.Valid(i) =>
        Valid(i)
      case officeSpecificationsResult =>
        val errors =
          List(officeSpecificationsResult.toEither.left.getOrElse(Nil))
        OfficeSpecificationsNotCreated.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(OfficeSpecificationsDatabaseError.invalidNel)
    }
  }

  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    officeSpecificationsRepo.delete(officeId)
  }
}

object OfficeSpecificationsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeSpecificationsRepo: OfficeSpecificationsRepositoryAlgebra[F]
                                                 ): OfficeSpecificationsServiceImpl[F] =
    new OfficeSpecificationsServiceImpl[F](officeSpecificationsRepo)
}

