package repository

import cats.data.Validated.Valid
import cats.effect.kernel.Ref
import cats.effect.IO
import java.time.LocalDateTime
import mocks.MockOfficeContactDetailsRepository
import models.database.CreateSuccess
import models.office.contact_details.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import repository.constants.OfficeContactDetailsConstants.*
import testData.TestConstants.*
import weaver.SimpleIOSuite
import services.RepositorySpecBase

object OfficeContactDetailsRepositorySpec extends SimpleIOSuite with RepositorySpecBase {

  test(".findByOfficeId() - should return the contact details if officeId1 exists") {

    val existingContactDetailsForUser = testContactDetails(businessId1, officeId1)

    for {
      mockRepo <- createMockRepo(List(existingContactDetailsForUser))
      result <- mockRepo.findByOfficeId(officeId1)
    } yield expect(result.contains(existingContactDetailsForUser))
  }

  test(".findByOfficeId() - should return None if officeId1 does not exist") {

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.findByOfficeId(officeId1)
    } yield expect(result.isEmpty)
  }

  test(".create() - when given a valid OfficeContactDetails should insert OfficeContactDetails data into the postgres db table") {

    val testCreateRequest = testCreateOfficeContactDetailsRequest(businessId1, officeId1)
    val testOfficeContactDetails = testContactDetails(businessId1, officeId1)

    for {
      mockRepo <- createMockRepo(List())
      result <- mockRepo.create(testCreateRequest)
      findInsertedContactDetails <- mockRepo.findByOfficeId(officeId1)
    } yield expect.all(
      result == Valid(CreateSuccess),
      findInsertedContactDetails == Some(testOfficeContactDetails)
    )
  }
}
