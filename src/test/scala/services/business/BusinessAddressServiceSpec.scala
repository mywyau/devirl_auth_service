package services.business

import cats.data.Validated.Valid
import cats.effect.IO
import models.business.business_address.errors.BusinessAddressNotFound
import models.business.business_address.requests.BusinessAddressRequest
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.address.{BusinessAddressService, BusinessAddressServiceImpl}
import services.business.mocks.MockBusinessAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressServiceSpec extends SimpleIOSuite {

  def testBusinessAddressRequest(userId: String, businessId: Option[String]): BusinessAddressRequest =
    BusinessAddressRequest(
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
      buildingName = Some("building name"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  def testBusinessAddress(id: Option[Int], userId: String, businessId: Option[String]): BusinessAddress =
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = Some("mikeyCorp"),
      buildingName = Some("building name"),
      floorNumber = Some("floor 1"),
      street = Some("1 Canton Street"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test(".getByBusinessId() - when there is an existing business address details given a businessId should return the correct address details - Right(address)") {

    val existingAddressForUser = testBusinessAddress(Some(1), "userId_1", Some("businessId_1"))

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map("businessId_1" -> existingAddressForUser))
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getByBusinessId("businessId_1")
    } yield {
      expect(result == Right(existingAddressForUser))
    }
  }

  test(".getByBusinessId() - when there are no existing business address details given a businessId should return Left(AddressNotFound)") {

    val existingAddressForUser = testBusinessAddress(Some(1), "userId_1", Some("businessId_1"))

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getByBusinessId("businessId_1")
    } yield {
      expect(result == Left(BusinessAddressNotFound))
    }
  }

  test(".created() - when given a BusinessAddress successfully create the address") {

    val testAddressRequest = testBusinessAddressRequest("userId_1", Some("businessId_1"))

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = BusinessAddressService(mockBusinessAddressRepository)

    for {
      result <- service.createAddress(testAddressRequest)
    } yield {
      expect(result == Valid(1))
    }
  }
}
