package services.office.office_contact_details

import cats.data.{Validated, ValidatedNel}
import cats.data.Validated.{Invalid, Valid}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.database.SqlErrors
import models.office.office_contact_details.OfficeContactDetails
import models.office.office_contact_details.errors.*
import repositories.office.OfficeContactDetailsRepositoryAlgebra


class OfficeContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                     officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
                                                                                   ) extends OfficeContactDetailsServiceAlgebra[F] {

  override def getContactDetailsByBusinessId(businessId: String): F[Either[OfficeContactDetailsErrors, OfficeContactDetails]] = {
    officeContactDetailsRepo.findByBusinessId(businessId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(OfficeContactDetailsNotFound))
    }
  }

  override def createOfficeContactDetails(officeContactDetails: OfficeContactDetails): F[ValidatedNel[OfficeContactDetailsErrors, Int]] = {

    val contactDetailsCreation: F[ValidatedNel[SqlErrors, Int]] =
      officeContactDetailsRepo.createContactDetails(officeContactDetails)

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
}

object OfficeContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   officeContactDetailsRepo: OfficeContactDetailsRepositoryAlgebra[F]
                                                 ): OfficeContactDetailsServiceImpl[F] =
    new OfficeContactDetailsServiceImpl[F](officeContactDetailsRepo)
}

