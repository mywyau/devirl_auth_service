package services.business

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.specifications.CreateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import models.database.CreateSuccess
import models.Monday
import models.OpeningHours
import models.Tuesday
import repositories.business.BusinessSpecificationsRepositoryAlgebra
import services.business.mocks.MockBusinessSpecificationsRepository
import testData.TestConstants.*
import weaver.SimpleIOSuite

object BusinessSpecificationsServiceSpec extends SimpleIOSuite {

  def testCreateBusinessSpecificationsRequest(
    userId: String,
    businessId: String
  ): CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = userId,
      businessId = businessId,
      businessName = businessName1,
      description = businessDescription1,
      openingHours = List(
        OpeningHours(
          day = Monday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        ),
        OpeningHours(
          day = Tuesday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        )
      )
    )

  def testSpecifications(
    id: Option[Int],
    userId: String,
    businessId: String
  ): BusinessSpecificationsPartial =
    BusinessSpecificationsPartial(
      userId = userId,
      businessId = businessId,
      businessName = Some("MikeyCorp"),
      description = Some("Some description"),
      openingHours = Some(
        List(
          OpeningHours(
            day = Monday,
            openingTime = openingTime0900,
            closingTime = closingTime1700
          ),
          OpeningHours(
            day = Tuesday,
            openingTime = openingTime0900,
            closingTime = closingTime1700
          )
        )
      )
    )

  test(".getByBusinessId() - when there is an existing BusinessSpecifications, given a business_id should return the correct Specifications - Right(Specifications)") {

    val existingSpecificationsForUser = testSpecifications(Some(1), "userId1", "business_1")

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map("business_1" -> existingSpecificationsForUser))
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.getByBusinessId("business_1")
    } yield expect(result == Some(existingSpecificationsForUser))
  }

  test(".getByBusinessId() - when there are no existing BusinessSpecifications, given a business_id should return Left(SpecificationsNotFound)") {

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map())
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.getByBusinessId("business_1")
    } yield expect(result == None)
  }

  test(".create() - when given a BusinessSpecifications, successfully create the BusinessSpecifications in Database") {

    val sampleSpecifications = testCreateBusinessSpecificationsRequest("userId1", "business_1")

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map())
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.create(sampleSpecifications)
    } yield expect(result == Valid(CreateSuccess))
  }
}
