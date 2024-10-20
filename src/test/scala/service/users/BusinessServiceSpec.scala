package service.users

import cats.effect.IO
import models.users.Business
import models.users.errors._
import repositories.BusinessRepositoryAlgebra
import services.BusinessServiceImpl
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockBusinessRepository extends BusinessRepositoryAlgebra[IO] {

  // Use a mutable Map to store businesses
  private var business: Map[String, Business] = Map.empty

  def withInitialBusiness(initial: Map[String, Business]): MockBusinessRepository = {
    val repository = new MockBusinessRepository
    repository.business = initial
    repository
  }

  // Get all businesses
  override def getAllBusiness: IO[List[Business]] = IO.pure(business.values.toList)

  // Find business by ID
  override def findBusinessById(businessId: String): IO[Option[Business]] = IO.pure(business.get(businessId))

  // Find business by name (This is corrected to search through the values)
  override def findBusinessByName(businessName: String): IO[Option[Business]] = {
    IO.pure(business.values.find(_.business_name == businessName))
  }

  // Set a new business
  override def setBusiness(newBusiness: Business): IO[Int] = {
    business += (newBusiness.business_id -> newBusiness)
    IO.pure(1)
  }

  // Update an existing business by ID
  override def updateBusiness(businessId: String, updatedBusiness: Business): IO[Int] = {
    if (business.contains(businessId)) {
      business += (businessId -> updatedBusiness)
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  // Delete a business by ID
  override def deleteBusiness(businessId: String): IO[Int] = {
    if (business.contains(businessId)) {
      business -= businessId
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }
}


object BusinessServiceSpec extends SimpleIOSuite {

  def freshRepository = new MockBusinessRepository

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      business_id = "business_1",
      business_name = "Sample Business 1",
      contact_number = "07402205071",
      contact_email = "business_1@gmail.com",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  val sampleBusiness_2: Business =
    Business(
      id = Some(1),
      business_id = "business_2",
      business_name = "Sample Business 2",
      contact_number = "02920362341",
      contact_email = "business_2@gmail.com",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )
  // Test case for creating a business
  test("create a new business successfully") {
    val mockRepository = freshRepository
    val businessService = new BusinessServiceImpl[IO](mockRepository)
    for {
      result <- businessService.createBusiness(sampleBusiness_1)
    } yield expect(result == Right(1))
  }

  // Test case for finding a business by ID
  test("find a business by business_id") {
    val mockRepository = freshRepository
    val businessService = new BusinessServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1) // Insert the business
      result <- businessService.findBusinessById("business_1")
    } yield expect(result == Right(sampleBusiness_1))
  }

  // Test case for finding a business that doesn't exist
  test("return an error if business ID does not exist") {
    val mockRepository = freshRepository
    val businessService = new BusinessServiceImpl[IO](mockRepository)
    for {
      result <- businessService.findBusinessById("nonexistent_id")
    } yield expect(result == Left(BusinessNotFound))
  }

  // Test case for updating a business
  test("update a business") {
    val mockRepository = freshRepository
    val businessService = new BusinessServiceImpl[IO](mockRepository)
    val updatedBusiness = sampleBusiness_1.copy(business_name = "Updated Business")
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      result <- businessService.updateBusiness("business_1", updatedBusiness)
    } yield expect(result == Right(1))
  }

  // Test case for deleting a business
  test("delete a business") {
    val mockRepository = freshRepository
    val businessService = new BusinessServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      result <- businessService.deleteBusiness("business_1")
    } yield expect(result == Right(1))
  }
}
