package controllers.users.wanderer_address.mocks

import cats.effect.IO
import models.users.*
import models.wanderer.wanderer_address.errors.{UserNotFound, WandererAddressErrors}
import models.wanderer.wanderer_address.service.WandererAddress
import services.wanderer_address.WandererAddressServiceAlgebra

class MockWandererAddressService(userAddressData: Map[String, WandererAddress])
  extends WandererAddressServiceAlgebra[IO] {

  override def getAddressDetailsByUserId(userId: String): IO[Either[WandererAddressErrors, WandererAddress]] = {
    userAddressData.get(userId) match {
      case Some(address) => IO.pure(Right(address))
      case None => IO.pure(Left(UserNotFound))
    }
  }

  override def createAddress(wandererAddress: WandererAddress): IO[Int] = ???
}