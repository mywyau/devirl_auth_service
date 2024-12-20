package controllers.business.business_address.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.address_details.errors.{BusinessAddressErrors, BusinessUserNotFound}
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import models.database.SqlErrors
import services.business.address.BusinessAddressServiceAlgebra


class MockBusinessAddressService(userAddressData: Map[String, BusinessAddress])
  extends BusinessAddressServiceAlgebra[IO] {

  override def getByBusinessId(businessId: String): IO[Either[BusinessAddressErrors, BusinessAddress]] = {
    userAddressData.get(businessId) match {
      case Some(address) => IO.pure(Right(address))
      case None => IO.pure(Left(BusinessUserNotFound))
    }
  }

  override def createAddress(request: BusinessAddressRequest): IO[ValidatedNel[SqlErrors, Int]] = IO.pure(Valid(1))
}