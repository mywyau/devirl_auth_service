package services.business

import cats.data.Validated.Valid
import cats.effect.IO
import models.business.address_details.errors.BusinessAddressNotFound
import models.business.address_details.requests.BusinessAddressRequest
import models.business.address_details.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.address.{BusinessAddressService, BusinessAddressServiceImpl}
import services.business.mocks.MockBusinessAddressRepository
import services.constants.BusinessAddressConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressServiceSpec extends SimpleIOSuite {

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
