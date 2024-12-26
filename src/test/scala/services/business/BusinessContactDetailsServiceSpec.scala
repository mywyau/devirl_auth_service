package services.business

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.errors.BusinessContactDetailsNotFound
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import services.business.contact_details.BusinessContactDetailsService
import services.business.mocks.MockBusinessContactDetailsRepository
import services.constants.BusinessContactDetailsServiceConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessContactDetailsServiceSpec extends SimpleIOSuite {

  test(".getContactDetailsByBusinessId() - when there is an existing user ContactDetails details given a business_id should return the correct ContactDetails - Right(ContactDetails)") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "user_id_1", "business_id_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map("business_id_1" -> existingContactDetailsForUser))
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_id_1")
    } yield {
      expect(result == Right(existingContactDetailsForUser))
    }
  }

  test(".getContactDetailsByBusinessId() - when there are no existing user ContactDetails details given a business_id should return Left(ContactDetailsNotFound)") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "user_id_1", "business_id_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_id_1")
    } yield {
      expect(result == Left(BusinessContactDetailsNotFound))
    }
  }

  test(".createBusinessContactDetails() - when given a BusinessContactDetails successfully create the ContactDetails") {

    val sampleContactDetails = testCreateBusinessContactDetailsRequest("user_id_1", "business_id_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.create(sampleContactDetails)
    } yield {
      expect(result == Valid(1))
    }
  }
}
