package controllers.business.business_address

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.address.BusinessAddressPartial
import models.business.address.errors.BusinessAddressErrors
import models.business.address.errors.BusinessUserNotFound
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.database.*
import services.business.BusinessAddressServiceAlgebra

class MockBusinessAddressService(userAddressData: Map[String, BusinessAddressPartial]) extends BusinessAddressServiceAlgebra[IO] {

  override def getByBusinessId(businessId: String): IO[Either[BusinessAddressErrors, BusinessAddressPartial]] =
    userAddressData.get(businessId) match {
      case Some(address) => IO.pure(Right(address))
      case None => IO.pure(Left(BusinessUserNotFound))
    }

  override def createAddress(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    IO.pure(Valid(CreateSuccess))

  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[BusinessAddressErrors, DatabaseSuccess]] =
    IO.pure(Valid(UpdateSuccess))

  override def delete(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    IO.pure(Valid(DeleteSuccess))
}
