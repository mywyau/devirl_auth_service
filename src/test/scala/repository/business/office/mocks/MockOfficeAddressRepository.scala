package repository.business.office.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.office.office_address.OfficeAddress
import repositories.office.OfficeAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockOfficeAddressRepository(ref: Ref[IO, List[OfficeAddress]]) extends OfficeAddressRepositoryAlgebra[IO] {

  override def findByBusinessId(businessId: String): IO[Option[OfficeAddress]] =
    ref.get.map(_.find(_.businessId == businessId))

  override def createOfficeAddress(officeAddress: OfficeAddress): IO[Int] =
    ref.modify(addresses => (officeAddress :: addresses, 1))
}
