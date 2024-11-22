package controllers.business.business_address.mocks

import cats.effect.IO
import models.business.business_address.errors.{BusinessAddressErrors, BusinessUserNotFound}
import models.business.business_address.service.BusinessAddress
import services.business.business_address.BusinessAddressServiceAlgebra


class MockBusinessAddressService(userAddressData: Map[String, BusinessAddress])
  extends BusinessAddressServiceAlgebra[IO] {

  override def getAddressDetailsByUserId(userId: String): IO[Either[BusinessAddressErrors, BusinessAddress]] = {
    userAddressData.get(userId) match {
      case Some(address) => IO.pure(Right(address))
      case None => IO.pure(Left(BusinessUserNotFound))
    }
  }

  override def createAddress(wandererAddress: BusinessAddress): IO[Int] = ???
}