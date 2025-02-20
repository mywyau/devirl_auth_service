package services.office

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.office.contact_details.OfficeContactDetails
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import services.ServiceSpecBase
import services.constants.OfficeContactDetailsServiceConstants.*
import services.office.OfficeContactDetailsService
import services.office.OfficeContactDetailsServiceImpl
import testData.TestConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeContactDetailsServiceSpec extends SimpleIOSuite with ServiceSpecBase {

  test(
    ".getByOfficeId() - " +
      "when there is an existing OfficeContactDetails given a officeId1 should return the correct ContactDetailsPartial - Right(ContactDetails)"
  ) {

    val existingContactDetailsForUser = testOfficeContactDetails(Some(1), businessId1, officeId1)

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map(officeId1 -> existingContactDetailsForUser))
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getByOfficeId(officeId1)
    } yield expect(result == Some(existingContactDetailsForUser))
  }

  test(
    ".getByOfficeId() - " +
      "when there are no existing OfficeContactDetails given a office_id should return Left(ContactDetailsNotFound)"
  ) {

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getByOfficeId(officeId1)
    } yield expect(result == None)
  }

  test(".create() - when given a OfficeContactDetails successfully create the ContactDetails") {

    val testCreateRequest = testCreateOfficeContactDetailsRequest(businessId1, officeId1)

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = OfficeContactDetailsService(mockOfficeContactDetailsRepository)

    for {
      result <- service.create(testCreateRequest)
    } yield expect(result == Valid(CreateSuccess))
  }
}
