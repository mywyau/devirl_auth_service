package services.business

import cats.effect.IO
import cats.data.Validated.Valid
import models.business.business_address.errors.BusinessAddressNotFound
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.business_address.{BusinessAddressService, BusinessAddressServiceImpl}
import services.business.mocks.MockBusinessAddressRepository
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressServiceSpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], userId: String): BusinessAddress =
    BusinessAddress(
      id = id,
      userId = userId,
      businessId = Some("business_id_1"),
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

  test(".getAddressDetailsByUserId() - when there is an existing user address details given a user_id should return the correct address details - Right(address)") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map("user_id_1" -> existingAddressForUser))
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getAddressDetailsByUserId("user_id_1")
    } yield {
      expect(result == Right(existingAddressForUser))
    }
  }

  test(".getAddressDetailsByUserId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testAddress(Some(1), "user_id_1")

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getAddressDetailsByUserId("user_id_1")
    } yield {
      expect(result == Left(BusinessAddressNotFound))
    }
  }

  test(".created() - when given a BusinessAddress successfully create the address") {

    val sampleAddress = testAddress(Some(1), "user_id_1")

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = BusinessAddressService(mockBusinessAddressRepository)

    for {
      result <- service.createAddress(sampleAddress)
    } yield {
      expect(result == Valid(1))
    }
  }
}
