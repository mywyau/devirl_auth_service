package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.ValidatedNel
import cats.effect.IO
import cats.effect.kernel.Ref
import models.database.SqlErrors
import models.office.address_details.OfficeAddress
import models.office.address_details.requests.CreateOfficeAddressRequest
import repositories.office.OfficeAddressRepositoryAlgebra

import java.time.LocalDateTime

case class MockOfficeAddressRepository(ref: Ref[IO, List[OfficeAddress]]) extends OfficeAddressRepositoryAlgebra[IO] {

  override def findByOfficeId(officeId: String): IO[Option[OfficeAddress]] =
    ref.get.map(_.find(_.officeId == officeId))

  override def create(officeAddressRequest: CreateOfficeAddressRequest): IO[ValidatedNel[SqlErrors, Int]] =
    ref.modify { address =>
      val updatedList =
        OfficeAddress(
          id = Some(1),
          businessId = officeAddressRequest.businessId,
          officeId = officeAddressRequest.officeId,
          buildingName = officeAddressRequest.buildingName,
          floorNumber = officeAddressRequest.floorNumber,
          street = officeAddressRequest.street,
          city = officeAddressRequest.city,
          country = officeAddressRequest.country,
          county = officeAddressRequest.county,
          postcode = officeAddressRequest.postcode,
          latitude = officeAddressRequest.latitude,
          longitude = officeAddressRequest.longitude,
          createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
          updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        )
          :: address
      (updatedList, validNel(1))
    }

  override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
}
