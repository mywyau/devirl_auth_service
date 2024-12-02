package services.business.mocks

import cats.data.ValidatedNel
import cats.data.Validated.Valid
import cats.effect.IO
import models.business.business_address.errors.BusinessAddressNotFound
import models.business.business_address.service.BusinessAddress
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.business_address.{BusinessAddressService, BusinessAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime


class MockBusinessAddressRepository(
                                     existingBusinessAddress: Map[String, BusinessAddress] = Map.empty
                                   ) extends BusinessAddressRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, BusinessAddress]] = IO.pure(existingBusinessAddress)

  override def findByUserId(userId: String): IO[Option[BusinessAddress]] = IO.pure(existingBusinessAddress.get(userId))

  override def createBusinessAddress(businessAddress: BusinessAddress): IO[ValidatedNel[SqlErrors, Int]] = IO.pure(Valid(1))
}