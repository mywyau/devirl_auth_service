package services.office.contact_details

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.database.SqlErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.errors.*
import repositories.office.OfficeContactDetailsRepositoryAlgebra


trait OfficeContactDetailsServiceAlgebra[F[_]] {

  def getByOfficeId(officeId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetails]]

  def create(officeContactDetails: OfficeContactDetails): F[cats.data.ValidatedNel[OfficeContactDetailsErrors, Int]]

  def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]]

}

class OfficeContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                     officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
                                                                                   ) extends OfficeContactDetailsServiceAlgebra[F] {

  override def getByOfficeId(officeId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetails]] = {
    officeContactDetailsRepo.findByOfficeId(officeId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeContactDetailsNotFound))
    }
  }

  override def create(officeContactDetails: OfficeContactDetails): F[ValidatedNel[OfficeContactDetailsErrors, Int]] = {

    val contactDetailsCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeContactDetailsRepo.create(officeContactDetails)

    contactDetailsCreation.map {
      case Validated.Valid(i) =>
        Valid(i)
      case contactDetailsResult =>
        val errors =
          List(contactDetailsResult.toEither.left.getOrElse(Nil))
        OfficeContactDetailsNotCreated.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(OfficeContactDetailsDatabaseError.invalidNel)
    }
  }

  override def delete(officeId: String): F[ValidatedNel[SqlErrors, Int]] = {
    officeContactDetailsRepo.delete(officeId)
  }
}

object OfficeContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
                                                 ): OfficeContactDetailsServiceImpl[F] =
    new OfficeContactDetailsServiceImpl[F](officeContactDetailsRepo)
}

