package repository.office.mocks

import cats.data.Validated.validNel
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.kernel.Ref
import cats.effect.IO
import java.time.LocalDateTime
import models.database.*
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial
import repositories.office.OfficeAddressRepositoryAlgebra

case class MockOfficeAddressRepository(ref: Ref[IO, List[OfficeAddressPartial]]) extends OfficeAddressRepositoryAlgebra[IO] {

  override def deleteAllByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))

  override def update(officeId: String, request: UpdateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(UpdateSuccess))

  override def findByOfficeId(officeId: String): IO[Option[OfficeAddressPartial]] =
    ref.get.map(_.find(_.officeId == officeId))

  override def create(officeAddressRequest: CreateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] =
    ref.modify { address =>
      val updatedList =
        OfficeAddressPartial(
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
          longitude = officeAddressRequest.longitude
        )
          :: address
      (updatedList, validNel(CreateSuccess))
    }

  override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))
}
