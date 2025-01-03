package services.business.mocks

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.address.BusinessAddress
import models.business.address.errors.BusinessAddressNotFound
import models.business.address.requests.CreateBusinessAddressRequest
import models.database.DatabaseErrors
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.address.{BusinessAddressService, BusinessAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime
import models.business.address.requests.UpdateBusinessAddressRequest


class MockBusinessAddressRepository(
                                     existingBusinessAddress: Map[String, BusinessAddress] = Map.empty
                                   ) extends BusinessAddressRepositoryAlgebra[IO] {


  override def update(businessId: String, request: UpdateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, Int]] = ???

  def showAllUsers: IO[Map[String, BusinessAddress]] = IO.pure(existingBusinessAddress)

  override def findByBusinessId(businessId: String): IO[Option[BusinessAddress]] = IO.pure(existingBusinessAddress.get(businessId))

  override def createBusinessAddress(request: CreateBusinessAddressRequest): IO[ValidatedNel[DatabaseErrors, Int]] = IO.pure(Valid(1))

  override def deleteBusinessAddress(businessId: String): IO[ValidatedNel[DatabaseErrors, Int]] = ???
}