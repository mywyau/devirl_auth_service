package services.business

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.business_contact_details.BusinessContactDetails
import models.business.business_contact_details.errors.BusinessContactDetailsNotFound
import models.database.SqlErrors
import repositories.business.BusinessContactDetailsRepositoryAlgebra
import services.business.business_contact_details.BusinessContactDetailsService
import services.business.mocks.MockBusinessContactDetailsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessContactDetailsServiceSpec extends SimpleIOSuite {

  def testContactDetails(id: Option[Int], userId: String, businessId: String, business_id: String): BusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = userId,
      businessId = businessId,
      businessName = "MikeyCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".getContactDetailsByBusinessId() - when there is an existing user ContactDetails details given a business_id should return the correct ContactDetails - Right(ContactDetails)") {

    val existingContactDetailsForUser = testContactDetails(Some(1),"user_id_1", "business_1", "business_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map("business_1" -> existingContactDetailsForUser))
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_1")
    } yield {
      expect(result == Right(existingContactDetailsForUser))
    }
  }

  test(".getContactDetailsByBusinessId() - when there are no existing user ContactDetails details given a business_id should return Left(ContactDetailsNotFound)") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "user_id_1", "business_1", "business_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_1")
    } yield {
      expect(result == Left(BusinessContactDetailsNotFound))
    }
  }

  test(".createBusinessContactDetails() - when given a BusinessContactDetails successfully create the ContactDetails") {

    val sampleContactDetails = testContactDetails(Some(1),"user_id_1", "business_1", "business_1")

    val mockBusinessContactDetailsRepository = new MockBusinessContactDetailsRepository(Map())
    val service = BusinessContactDetailsService[IO](mockBusinessContactDetailsRepository)

    for {
      result <- service.createBusinessContactDetails(sampleContactDetails)
    } yield {
      expect(result == Valid(1))
    }
  }
}
