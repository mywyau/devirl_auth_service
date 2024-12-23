package services.office.address

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import models.database.SqlErrors
import models.office.address_details.OfficeAddress
import models.office.address_details.errors.OfficeAddressNotFound
import models.office.address_details.requests.OfficeAddressRequest
import repositories.office.OfficeAddressRepositoryAlgebra
import services.office.address.{OfficeAddressService, OfficeAddressServiceImpl}
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object OfficeAddressServiceSpec extends SimpleIOSuite {

  def testAddress(id: Option[Int], businessId: String, office_id: String): OfficeAddress =
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = office_id,
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
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

  def testAddressRequest(businessId: String, office_id: String): OfficeAddressRequest =
    OfficeAddressRequest(
      businessId = businessId,
      officeId = office_id,
      buildingName = Some("build_123"),
      floorNumber = Some("floor 1"),
      street = Some("123 Main Street"),
      city = Some("New York"),
      country = Some("USA"),
      county = Some("New York County"),
      postcode = Some("10001"),
      latitude = Some(100.1),
      longitude = Some(-100.1)
    )

  class MockOfficeAddressRepository(
                                     existingOfficeAddress: Map[String, OfficeAddress] = Map.empty
                                   ) extends OfficeAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeAddress]] = IO.pure(existingOfficeAddress)

    override def findByOfficeId(officeId: String): IO[Option[OfficeAddress]] = IO.pure(existingOfficeAddress.get(officeId))

    override def create(officeAddressRequest: OfficeAddressRequest): IO[ValidatedNel[SqlErrors, Int]] = IO(Valid(1))

    override def delete(officeId: String): IO[ValidatedNel[SqlErrors, Int]] = ???
  }


  test(".getAddressByBusinessId() - when there is an existing user address details given a user_id should return the correct address details - Right(address)") {

    val existingAddressForUser = testAddress(Some(1), "business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map("business_1" -> existingAddressForUser))
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.getByOfficeId("business_1")
    } yield {
      expect(result == Right(existingAddressForUser))
    }
  }

  test(".getAddressByBusinessId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testAddress(Some(1), "business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.getByOfficeId("business_1")
    } yield {
      expect(result == Left(OfficeAddressNotFound))
    }
  }

  test(".createOfficeAddress() - when given a OfficeAddress successfully create the address") {

    val sampleAddress = testAddressRequest("business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = OfficeAddressService(mockOfficeAddressRepository)

    for {
      result <- service.create(sampleAddress)
    } yield {
      expect(result == Valid(1))
    }
  }
}
