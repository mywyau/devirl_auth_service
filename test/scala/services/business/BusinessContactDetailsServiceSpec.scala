package services.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime
import models.business.contact_details.errors.BusinessContactDetailsNotFound
import models.business.contact_details.BusinessContactDetails
import models.database.CreateSuccess
import models.database.DatabaseErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import services.business.mocks.MockBusinessContactDetailsRepository
import services.constants.BusinessContactDetailsServiceConstants.*
import weaver.SimpleIOSuite

object BusinessContactDetailsServiceSpec extends SimpleIOSuite {

  test(".getByBusinessId() - when there is an existing user ContactDetails details given a business_id should return the correct ContactDetails - Right(ContactDetails)") {

    val existingContactDetailsForUser = testContactDetails("userId1", "businessId1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map("businessId1" -> existingContactDetailsForUser))
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getByBusinessId("businessId1")
    } yield expect(result == Right(existingContactDetailsForUser))
  }

  test(".getByBusinessId() - when there are no existing user ContactDetails details given a business_id should return Left(ContactDetailsNotFound)") {

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getByBusinessId("businessId1")
    } yield expect(result == Left(BusinessContactDetailsNotFound))
  }

  test(".createBusinessContactDetails() - when given a BusinessContactDetails successfully create the ContactDetails") {

    val sampleContactDetails = testCreateBusinessContactDetailsRequest("userId1", "businessId1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.create(sampleContactDetails)
    } yield expect(result == Valid(CreateSuccess))
  }
}
