package services.business

import cats.data.Validated.Valid
import cats.effect.IO
import models.business.address.errors.BusinessAddressNotFound
import models.business.address.requests.CreateBusinessAddressRequest
import models.database.CreateSuccess
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.mocks.MockBusinessAddressRepository
import services.constants.BusinessAddressServiceConstants.*
import testData.TestConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressServiceSpec extends SimpleIOSuite {

  test(".getByBusinessId() - when there is an existing business address details given a businessId should return the correct address details - Right(address)") {

    val existingAddressForUser = testBusinessAddress(userId1, businessId1)

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map(businessId1 -> existingAddressForUser))
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getByBusinessId(businessId1)
    } yield expect(result == Right(existingAddressForUser))
  }

  test(".getByBusinessId() - when there are no existing business address details given a businessId should return Left(AddressNotFound)") {

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = new BusinessAddressServiceImpl[IO](mockBusinessAddressRepository)

    for {
      result <- service.getByBusinessId(businessId1)
    } yield expect(result == Left(BusinessAddressNotFound))
  }

  test(".created() - when given a BusinessAddress successfully create the address") {

    val testAddressRequest = testBusinessAddressRequest(userId1, businessId1)

    val mockBusinessAddressRepository = new MockBusinessAddressRepository(Map())
    val service = BusinessAddressService(mockBusinessAddressRepository)

    for {
      result <- service.createAddress(testAddressRequest)
    } yield expect(result == Valid(CreateSuccess))
  }
}
