package controllers.business.business_address.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.address.BusinessAddress
import models.business.address.errors.{BusinessAddressErrors, BusinessUserNotFound}
import models.business.address.requests.CreateBusinessAddressRequest
import models.database.DatabaseErrors
import services.business.address.BusinessAddressServiceAlgebra
import models.business.address.requests.UpdateBusinessAddressRequest


class MockBusinessAddressService(userAddressData: Map[String, BusinessAddress])
  extends BusinessAddressServiceAlgebra[IO] {


  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[BusinessAddressErrors, Int]] = ???

  override def getByBusinessId(businessId: String): IO[Either[BusinessAddressErrors, BusinessAddress]] = {
    userAddressData.get(businessId) match {
      case Some(address) => IO.pure(Right(address))
      case None => IO.pure(Left(BusinessUserNotFound))
    }
  }

  override def createAddress(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, Int]] = IO.pure(Valid(1))


  override def deleteAddress(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???
}