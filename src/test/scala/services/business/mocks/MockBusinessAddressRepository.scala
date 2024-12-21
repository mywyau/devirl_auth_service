package services.business.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.address_details.errors.BusinessAddressNotFound
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.address.{BusinessAddressService, BusinessAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime


class MockBusinessAddressRepository(
                                     existingBusinessAddress: Map[String, BusinessAddress] = Map.empty
                                   ) extends BusinessAddressRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessAddress]] = IO.pure(existingBusinessAddress)

  override def findByBusinessId(businessId: String): IO[Option[BusinessAddress]] = IO.pure(existingBusinessAddress.get(businessId))

  override def createBusinessAddress(request: BusinessAddressRequest): IO[ValidatedNel[SqlErrors, Int]] = IO.pure(Valid(1))

  override def deleteBusinessAddress(businessId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}