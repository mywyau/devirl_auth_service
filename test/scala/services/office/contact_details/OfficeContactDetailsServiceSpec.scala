package services.office.contact_details

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.DatabaseErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.errors.OfficeContactDetailsNotFound
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import services.office.contact_details.{OfficeContactDetailsService, OfficeContactDetailsServiceImpl}
import weaver.SimpleIOSuite
import services.constants.OfficeContactDetailsServiceConstants.*

import java.time.LocalDateTime
import models.database.CreateSuccess

object OfficeContactDetailsServiceSpec extends SimpleIOSuite {

  test(
    ".getByOfficeId() - " +
      "when there is an existing OfficeContactDetails given a office_id_1 should return the correct ContactDetailsPartial - Right(ContactDetails)"
  ) {

    val existingContactDetailsForUser = testOfficeContactDetails(Some(1), "business__id_1", "office_id_1")

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map("office_id_1" -> existingContactDetailsForUser))
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getByOfficeId("office_id_1")
    } yield {
      expect(result == Right(existingContactDetailsForUser))
    }
  }

  test(
    ".getByOfficeId() - " +
      "when there are no existing OfficeContactDetails given a office_id should return Left(ContactDetailsNotFound)"
  ) {

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getByOfficeId("office_id_1")
    } yield {
      expect(result == Left(OfficeContactDetailsNotFound))
    }
  }

  test(".create() - when given a OfficeContactDetails successfully create the ContactDetails") {

    val testCreateRequest = testCreateOfficeContactDetailsRequest("business__id_1", "office_id_1")

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = OfficeContactDetailsService(mockOfficeContactDetailsRepository)

    for {
      result <- service.create(testCreateRequest)
    } yield {
      expect(result == Valid(CreateSuccess))
    }
  }
}
