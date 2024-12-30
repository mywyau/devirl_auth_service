package repository.constants

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repository.business.mocks.MockBusinessContactDetailsRepository
import repository.office.mocks.MockOfficeContactDetailsRepository

import java.time.LocalDateTime

object OfficeContactDetailsConstants {

  def testCreateOfficeContactDetailsRequest(businessId: String, office_id: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071"
    )

  def testContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mikey@gmail.com"),
      contactNumber = Some("07402205071"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeContactDetails]): IO[MockOfficeContactDetailsRepository] =
    Ref.of[IO, List[OfficeContactDetails]](initialUsers).map(MockOfficeContactDetailsRepository.apply)

}
