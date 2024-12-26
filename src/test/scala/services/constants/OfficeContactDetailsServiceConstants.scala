package services.constants

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.SqlErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import repository.business.mocks.MockBusinessContactDetailsRepository
import repository.office.mocks.MockOfficeContactDetailsRepository

import java.time.LocalDateTime

object OfficeContactDetailsServiceConstants {

  def testOfficeContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetails =
    OfficeContactDetails(
      id = Some(1),
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
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
                                            existingOfficeContactDetails: Map[String, OfficeContactDetails] = Map.empty
                                          ) extends OfficeContactDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeContactDetails]] = IO.pure(existingOfficeContactDetails)

    override def findByOfficeId(officeId: String): IO[Option[OfficeContactDetails]] = IO.pure(existingOfficeContactDetails.get(officeId))

    override def create(createOfficeContactDetailsRequest: CreateOfficeContactDetailsRequest): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

    override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }

}
