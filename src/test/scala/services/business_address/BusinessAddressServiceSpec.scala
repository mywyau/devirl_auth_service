package services.business_address

import cats.effect.IO
import models.business.business_address.errors.BusinessAddressNotFound
import models.business.business_address.service.BusinessAddress
import repositories.business.BusinessAddressRepositoryAlgebra
import services.business.business_address.{BusinessAddressService, BusinessAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object BusinessAddressServiceSpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], userId: String): BusinessAddress =
    BusinessAddress(
      id = Some(1),
      userId = userId,
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("UK"),
      county = Some("County 1"),
      postcode = Some("CF3 3NJ"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  class MockBusinessAddressRepository(
                                       existingBusinessAddress: Map[String, BusinessAddress] = Map.empty
                                     ) extends BusinessAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, BusinessAddress]] = IO.pure(existingBusinessAddress)

    override def createUserAddress(user: BusinessAddress): IO[Int] = IO.pure(1) // Assume user creation always succeeds

    override def findByUserId(userId: String): IO[Option[BusinessAddress]] = IO.pure(existingBusinessAddress.get(userId))

    override def updateAddressDynamic(userId: String, street: Option[String], city: Option[String], country: Option[String], county: Option[String], postcode: Option[String]): IO[Option[BusinessAddress]] = ???

    override def createRegistrationBusinessAddress(userId: String): IO[Int] =
      IO.pure(1)
  }


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
      expect(result == 1)
    }
  }
}
