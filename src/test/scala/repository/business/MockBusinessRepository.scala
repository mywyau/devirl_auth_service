package repository.business

import cats.effect.IO
import models.business.Business
import repositories.business.BusinessRepositoryAlgebra

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
