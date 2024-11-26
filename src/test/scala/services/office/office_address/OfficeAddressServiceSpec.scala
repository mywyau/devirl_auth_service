package services.office.office_address

import cats.effect.IO
import models.office.office_address.errors.OfficeAddressNotFound
import models.office.office_address.OfficeAddress
import repositories.office.OfficeAddressRepositoryAlgebra
import services.office.office_address.{OfficeAddressService, OfficeAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeAddressServiceSpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], businessId: String, office_id: String): OfficeAddress =
    OfficeAddress(
      id = Some(10),
      businessId = "business_1",
      office_id = "office_1",
      building_name = Some("build_123"),
      floor_number = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  class MockOfficeAddressRepository(
                                       existingOfficeAddress: Map[String, OfficeAddress] = Map.empty
                                     ) extends OfficeAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeAddress]] = IO.pure(existingOfficeAddress)

    override def findByBusinessId(userId: String): IO[Option[OfficeAddress]] = IO.pure(existingOfficeAddress.get(userId))

    override def createOfficeAddress(user: OfficeAddress): IO[Int] = IO.pure(1) // Assume user creation always succeeds
  }


  test(".getAddressByBusinessId() - when there is an existing user address details given a user_id should return the correct address details - Right(address)") {

    val existingAddressForUser = testAddress(Some(1), "business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map("business_1" -> existingAddressForUser))
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.getAddressByBusinessId("business_1")
    } yield {
      expect(result == Right(existingAddressForUser))
    }
  }

  test(".getAddressByBusinessId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testAddress(Some(1), "business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.getAddressByBusinessId("business_1")
    } yield {
      expect(result == Left(OfficeAddressNotFound))
    }
  }

  test(".createOfficeAddress() - when given a OfficeAddress successfully create the address") {

    val sampleAddress = testAddress(Some(1), "business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = OfficeAddressService(mockOfficeAddressRepository)

    for {
      result <- service.createOfficeAddress(sampleAddress)
    } yield {
      expect(result == 1)
    }
  }
}
