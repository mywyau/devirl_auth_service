package repository.business.mocks

import cats.data.ValidatedNel
import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.business_address.service.BusinessAddress
import models.database.SqlErrors
import repositories.business.BusinessAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockBusinessAddressRepository(ref: Ref[IO, List[BusinessAddress]]) extends BusinessAddressRepositoryAlgebra[IO] {

  override def findByUserId(userId: String): IO[Option[BusinessAddress]] =
    ref.get.map(_.find(_.userId == userId))

  override def createBusinessAddress(businessAddress: BusinessAddress): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify(addresses => (businessAddress :: addresses, Valid(1)))

}
