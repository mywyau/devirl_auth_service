package services.business

import cats.data.Validated.{Invalid, Valid}
import cats.data.ValidatedNel
import cats.effect.IO
import models.business.business_specs.errors.BusinessSpecificationsNotFound
import models.business.business_specs.BusinessSpecifications
import models.database.SqlErrors
import repositories.business.BusinessSpecsRepositoryAlgebra
import services.business.specifications.BusinessSpecificationsService
import services.business.mocks.MockBusinessSpecificationsRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessSpecificationsServiceSpec extends SimpleIOSuite {

  def testSpecifications(
                          id: Option[Int],
                          userId: String,
                          businessId: String
                        ): BusinessSpecifications =
    BusinessSpecifications(
      id = Some(1),
      userId = userId,
      businessId = businessId,
      businessName = "MikeyCorp",
      description = "Some description",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".getByBusinessId() - when there is an existing BusinessSpecifications, given a business_id should return the correct Specifications - Right(Specifications)") {

    val existingSpecificationsForUser = testSpecifications(Some(1), "user_id_1", "business_1")

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map("business_1" -> existingSpecificationsForUser))
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.getByBusinessId("business_1")
    } yield {
      expect(result == Right(existingSpecificationsForUser))
    }
  }

  test(".getByBusinessId() - when there are no existing BusinessSpecifications, given a business_id should return Left(SpecificationsNotFound)") {

    val existingSpecificationsForUser = testSpecifications(Some(1), "user_id_1", "business_1")

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map())
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.getByBusinessId("business_1")
    } yield {
      expect(result == Left(BusinessSpecificationsNotFound))
    }
  }

  test(".create() - when given a BusinessSpecifications, successfully create the BusinessSpecifications in Database") {

    val sampleSpecifications = testSpecifications(Some(1), "user_id_1", "business_1")

    val mockBusinessSpecificationsRepository = new MockBusinessSpecificationsRepository(Map())
    val service = BusinessSpecificationsService[IO](mockBusinessSpecificationsRepository)

    for {
      result <- service.create(sampleSpecifications)
    } yield {
      expect(result == Valid(1))
    }
  }
}
