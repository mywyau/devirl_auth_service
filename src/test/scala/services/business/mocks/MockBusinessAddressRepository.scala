package services.business.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.business_address.errors.BusinessAddressNotFound
import models.business.business_address.requests.BusinessAddressRequest
import models.business.business_address.service.BusinessAddress
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
}