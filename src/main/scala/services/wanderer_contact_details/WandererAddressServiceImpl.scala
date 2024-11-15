package services.wanderer_contact_details

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.contact_details.errors.{ContactDetailsErrors, ContactDetailsNotFound}
import models.users.contact_details.service.WandererContactDetails
import repositories.users.WandererContactDetailsRepositoryAlgebra


class WandererContactDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                       wandererContactDetailsRepo: WandererContactDetailsRepositoryAlgebra[F]
                                                                                     ) extends WandererContactDetailsServiceAlgebra[F] {

  override def getContactDetailsByUserId(user_id: String): F[Either[ContactDetailsErrors, WandererContactDetails]] = {
    wandererContactDetailsRepo.findByUserId(user_id).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(ContactDetailsNotFound))
    }
  }

  override def createContactDetails(wandererContactDetails: WandererContactDetails): F[Int] = {
    wandererContactDetailsRepo.createContactDetails(wandererContactDetails)
  }
}

object WandererContactDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   wandererContactDetailsRepo: WandererContactDetailsRepositoryAlgebra[F]
                                                 ): WandererContactDetailsServiceImpl[F] =
    new WandererContactDetailsServiceImpl[F](wandererContactDetailsRepo)
}

