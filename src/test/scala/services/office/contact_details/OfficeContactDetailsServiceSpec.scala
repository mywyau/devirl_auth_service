package services.office.contact_details

import cats.effect.IO
import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import models.database.SqlErrors
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.errors.OfficeContactDetailsNotFound
import repositories.office.OfficeContactDetailsRepositoryAlgebra
import services.office.contact_details.{OfficeContactDetailsService, OfficeContactDetailsServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeContactDetailsServiceSpec extends SimpleIOSuite {

  def testContactDetails(id: Option[Int], businessId: String, office_id: String): OfficeContactDetails =
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

  class MockOfficeContactDetailsRepository(
                                            existingOfficeContactDetails: Map[String, OfficeContactDetails] = Map.empty
                                          ) extends OfficeContactDetailsRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeContactDetails]] = IO.pure(existingOfficeContactDetails)

    override def findByBusinessId(businessId: String): IO[Option[OfficeContactDetails]] = IO.pure(existingOfficeContactDetails.get(businessId))

    override def createContactDetails(officeContactDetails: OfficeContactDetails): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

  }


  test(".getContactDetailsByBusinessId() - when there is an existing user ContactDetails details given a business_id should return the correct ContactDetails - Right(ContactDetails)") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "business_1", "office_1")

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map("business_1" -> existingContactDetailsForUser))
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_1")
    } yield {
      expect(result == Right(existingContactDetailsForUser))
    }
  }

  test(".getContactDetailsByBusinessId() - when there are no existing user ContactDetails details given a business_id should return Left(ContactDetailsNotFound)") {

    val existingContactDetailsForUser = testContactDetails(Some(1), "business_1", "office_1")

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = new OfficeContactDetailsServiceImpl[IO](mockOfficeContactDetailsRepository)

    for {
      result <- service.getContactDetailsByBusinessId("business_1")
    } yield {
      expect(result == Left(OfficeContactDetailsNotFound))
    }
  }

  test(".createOfficeContactDetails() - when given a OfficeContactDetails successfully create the ContactDetails") {

    val sampleContactDetails = testContactDetails(Some(1), "business_1", "office_1")

    val mockOfficeContactDetailsRepository = new MockOfficeContactDetailsRepository(Map())
    val service = OfficeContactDetailsService(mockOfficeContactDetailsRepository)

    for {
      result <- service.createOfficeContactDetails(sampleContactDetails)
    } yield {
      expect(result == Valid(1))
    }
  }
}
