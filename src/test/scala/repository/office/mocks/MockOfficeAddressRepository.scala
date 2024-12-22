package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.database.SqlErrors
import models.office.address_details.OfficeAddress
import repositories.office.OfficeAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockOfficeAddressRepository(ref: Ref[IO, List[OfficeAddress]]) extends OfficeAddressRepositoryAlgebra[IO] {

  override def findByOfficeId(officeId: String): IO[Option[OfficeAddress]] =
    ref.get.map(_.find(_.officeId == officeId))

  override def create(officeAddress: OfficeAddress): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { address =>
      val updatedList = officeAddress :: address
      (updatedList, validNel(1))
    }

  override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}
