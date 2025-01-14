package mocks

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import cats.syntax.all.*
import models.database.*
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddressPartial
import repositories.office.OfficeAddressRepositoryAlgebra

class MockOfficeAddressRepository(
  existingOfficeAddress: Map[String, OfficeAddressPartial] = Map.empty
) extends OfficeAddressRepositoryAlgebra[IO] {

  def showAllUsers: IO[Map[String, OfficeAddressPartial]] = IO.pure(existingOfficeAddress)

  override def findByOfficeId(officeId: String): IO[ValidatedNel[DatabaseErrors, OfficeAddressPartial]] = {
    val result = existingOfficeAddress.get(officeId) match {
      case Some(officeAddress) => Valid(officeAddress).toValidatedNel
      case None => NotFoundError.invalidNel
    }

    IO.pure(result)
  }

  override def create(request: CreateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(CreateSuccess))

  override def update(officeId: String, request: UpdateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(UpdateSuccess))

  override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))

  override def deleteAllByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))

}
