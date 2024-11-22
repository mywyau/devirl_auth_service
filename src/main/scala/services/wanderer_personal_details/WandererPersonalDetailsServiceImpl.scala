package services.wanderer_personal_details

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.wanderer_personal_details.errors.{PersonalDetailsErrors, PersonalDetailsNotFound}
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.user_profile.WandererPersonalDetailsRepositoryAlgebra


class WandererPersonalDetailsServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                       wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F]
                                                                                     ) extends WandererPersonalDetailsServiceAlgebra[F] {

  override def getPersonalDetailsByUserId(userId: String): F[Either[PersonalDetailsErrors, WandererPersonalDetails]] = {
    wandererPersonalDetailsRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(PersonalDetailsNotFound))
    }
  }

  override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): F[Int] = {
    wandererPersonalDetailsRepo.createPersonalDetails(wandererPersonalDetails)
  }
}

object WandererPersonalDetailsService {

  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   wandererPersonalDetailsRepo: WandererPersonalDetailsRepositoryAlgebra[F]
                                                 ): WandererPersonalDetailsServiceImpl[F] =
    new WandererPersonalDetailsServiceImpl[F](wandererPersonalDetailsRepo)
}

