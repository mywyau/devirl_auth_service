package repository.users

import models.users.Business
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessRepositorySpec extends SimpleIOSuite {

  def freshRepository = new repository.users.MockBusinessRepository

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
  test(".findBusinessById() - find a business by it's business_id") {
    val mockRepository: repository.users.MockBusinessRepository = freshRepository
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      _ <- mockRepository.setBusiness(sampleBusiness_2)
      result <- mockRepository.findBusinessById("business_1")
    } yield expect(result == Some(sampleBusiness_1))
  }

  // Test case for finding a business that doesn't exist
  test(".findBusinessById() - return an error if business ID does not exist") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      _ <- mockRepository.setBusiness(sampleBusiness_2)
      result <- mockRepository.findBusinessById("business_3")
    } yield expect(result == None)
  }

  test(".updateBusiness() - update a business") {
    val mockRepository = freshRepository
    val updatedBusiness = sampleBusiness_1.copy(business_name = "Updated Business")
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      _ <- mockRepository.setBusiness(sampleBusiness_2)
      result <- mockRepository.updateBusiness("business_1", updatedBusiness)
      updatedBusiness <- mockRepository.findBusinessById("business_1")
    } yield
      expect.all(
        result == 1, // Assert that update returned 1
        updatedBusiness == updatedBusiness // Assert the updated business matches expected values
      )
  }

  // Test case for deleting a business
  test("delete a business") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setBusiness(sampleBusiness_1)
      result <- mockRepository.deleteBusiness("business_1")
      notFoundBusiness <- mockRepository.findBusinessById("business_1")
    } yield
      expect.all(
        result == 1, // Assert that delete returned a count for 1 deleted business
        notFoundBusiness.isEmpty // Assert the business has been deleted and None is returned
      )
  }
}
