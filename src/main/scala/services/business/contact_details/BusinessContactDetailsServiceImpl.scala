package services.business.contact_details

import cats.data.Validated.{Invalid, Valid}
import cats.data.{Validated, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.*
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra


class BusinessContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                       businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
                                                                                     ) extends BusinessContactDetailsServiceAlgebra[F] {

  override def getContactDetailsByBusinessId(businessId: String): F[Either[BusinessContactDetailsErrors, BusinessContactDetails]] = {
    businessContactDetailsRepo.findByBusinessId(businessId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(BusinessContactDetailsNotFound))
    }
  }

  override def createBusinessContactDetails(businessContactDetails: BusinessContactDetails): F[ValidatedNel[BusinessContactDetailsErrors, Int]] = {

    val contactDetailsCreation: F[ValidatedNel[SqlErrors, Int]] =
      businessContactDetailsRepo.createContactDetails(businessContactDetails)

    contactDetailsCreation.map {
      case Validated.Valid(i) =>
        Valid(i)
      case contactDetailsResult =>
        val errors =
          List(contactDetailsResult.toEither.left.getOrElse(Nil))
        BusinessContactDetailsNotCreated.invalidNel
    }.handleErrorWith { e =>
      Concurrent[F].pure(BusinessContactDetailsDatabaseError.invalidNel)
    }
  }
}

object BusinessContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   businessContactDetailsRepo: BusinessContactDetailsRepositoryAlgebra[F]
                                                 ): BusinessContactDetailsServiceImpl[F] =
    new BusinessContactDetailsServiceImpl[F](businessContactDetailsRepo)
}

