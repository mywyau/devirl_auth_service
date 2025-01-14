package services.constants

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime
import models.database.*
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.requests.UpdateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import mocks.MockBusinessContactDetailsRepository
import mocks.MockOfficeContactDetailsRepository
import testData.TestConstants.*

object OfficeContactDetailsServiceConstants  {

  def testOfficeContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mike@gmail.com"),
      contactNumber = Some("07402205071")
    )

  def testCreateOfficeContactDetailsRequest(businessId: String, office_id: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071"
    )

  class MockOfficeContactDetailsRepository(
    existingOfficeContactDetailsPartial: Map[String, OfficeContactDetailsPartial] = Map.empty
  ) extends OfficeContactDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeContactDetailsPartial]] = IO.pure(existingOfficeContactDetailsPartial)

    override def findByOfficeId(officeId: String): IO[Option[OfficeContactDetailsPartial]] = IO.pure(existingOfficeContactDetailsPartial.get(officeId))

    override def update(officeId: String, request: UpdateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

    override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(CreateSuccess))

    override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???

    override def deleteAllByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = ???
  }

}
