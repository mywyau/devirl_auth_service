package repository.office

import cats.data.Validated.Valid
import cats.effect.IO
import cats.effect.kernel.Ref
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import repository.constants.OfficeContactDetailsConstants.*
import repository.office.mocks.MockOfficeContactDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeContactDetailsRepositorySpec extends SimpleIOSuite {

  test(".findByOfficeId() - should return the contact details if office_id_1 exists") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "business_id_1", "office_id_1")

    for {
      mockRepo <- createMockRepo(List(existingContactDetailsForUser))
      result <- mockRepo.findByOfficeId("office_id_1")
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByOfficeId() - should return None if office_id_1 does not exist") {

    for {
      mockRepo <- createMockRepo(List()) // No users initially
      result <- mockRepo.findByOfficeId("office_id_1")
    } yield expect(result.isEmpty)
  }

  test(".create() - when given a valid OfficeContactDetails should insert OfficeContactDetails data into the postgres db table") {

    val testCreateRequest = testCreateOfficeContactDetailsRequest("business_id_1", "office_id_1")
    val testOfficeContactDetails = testContactDetails(Some(1), "business_id_1", "office_id_1")

    for {
      mockRepo <- createMockRepo(List()) // No users initially
      result <- mockRepo.create(testCreateRequest)
      findInsertedContactDetails <- mockRepo.findByOfficeId("office_id_1")
    } yield expect.all(
      result == Valid(1),
      findInsertedContactDetails == Some(testOfficeContactDetails)
    )
  }
}
