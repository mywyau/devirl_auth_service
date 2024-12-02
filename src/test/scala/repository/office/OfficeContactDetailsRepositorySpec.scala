package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.office.office_contact_details.OfficeContactDetails
import repository.office.mocks.MockOfficeContactDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeContactDetailsRepositorySpec extends SimpleIOSuite {

  def testContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = office_id,
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mikey@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  // Helper method to create a mock repository with initial state
  def createMockRepo(initialUsers: List[OfficeContactDetails]): IO[MockOfficeContactDetailsRepository] =
    Ref.of[IO, List[OfficeContactDetails]](initialUsers).map(MockOfficeContactDetailsRepository.apply)

  test(".findByBusinessId() - should return the contact details if business_id exists") {
    val existingContactDetailsForUser = testContactDetails(Some(1), "business_id_1", "office_1")

    for {
      mockRepo <- createMockRepo(List(existingContactDetailsForUser))
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByBusinessId() - should return None if business_id does not exist") {
    for {
      mockRepo <- createMockRepo(Nil) // No users initially
      result <- mockRepo.findByBusinessId("business_id_1")
    } yield expect(result.isEmpty)
  }

  test(".createContactDetails() - when given a valid OfficeContactDetails should insert OfficeContactDetails data into the postgres db table") {

    val testContactDetailsForUser2: OfficeContactDetails = testContactDetails(Some(2), "business_id_2", "office_2")
    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.createContactDetails(testContactDetailsForUser2)
      findInsertedContactDetails <- mockRepo.findByBusinessId("business_id_2")
    } yield expect.all(
      result == Valid(1),
      findInsertedContactDetails == Some(testContactDetailsForUser2)
    )
  }
}
