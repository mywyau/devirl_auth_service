package services.wanderer_address

import cats.effect.Concurrent
import cats.implicits.*
import cats.{Monad, NonEmptyParallel}
import models.users.*
import models.users.wanderer_address.errors.{AddressNotFound, WandererAddressErrors}
import models.users.wanderer_address.service.WandererAddress
import repositories.users.WandererAddressRepositoryAlgebra


class WandererAddressServiceImpl[F[_] : Concurrent : NonEmptyParallel : Monad](
                                                                                wandererAddressRepo: WandererAddressRepositoryAlgebra[F]
                                                                              ) extends WandererAddressServiceAlgebra[F] {

  override def getAddressDetailsByUserId(userId: String): F[Either[WandererAddressErrors, WandererAddress]] = {
    wandererAddressRepo.findByUserId(userId).flatMap {
      case Some(user) =>
        Concurrent[F].pure(Right(user))
      case None =>
        Concurrent[F].pure(Left(AddressNotFound))
    }
  }

  override def createAddress(wandererAddress: WandererAddress): F[Int] = {
    wandererAddressRepo.createUserAddress(wandererAddress)
  }
}

object WandererAddressService {
  
  def apply[F[_] : Concurrent : NonEmptyParallel](
                                                   wandererAddressRepo: WandererAddressRepositoryAlgebra[F]
                                                 ): WandererAddressServiceImpl[F] =
    new WandererAddressServiceImpl[F](wandererAddressRepo)
}

