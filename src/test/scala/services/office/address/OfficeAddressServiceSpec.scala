package services.office.address

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import java.time.LocalDateTime
import models.database.CreateSuccess
import models.database.DatabaseErrors
import models.database.DatabaseSuccess
import models.database.DeleteSuccess
import models.database.UpdateSuccess
import models.office.address_details.errors.OfficeAddressNotFound
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.requests.UpdateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.office.OfficeAddressRepositoryAlgebra
import services.office.address.OfficeAddressService
import services.office.address.OfficeAddressServiceImpl
import weaver.SimpleIOSuite

object OfficeAddressServiceSpec extends SimpleIOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  def testOfficeAddressPartial(businessId: String, office_id: String): OfficeAddressPartial =
    OfficeAddressPartial(
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

  def testAddressRequest(businessId: String, office_id: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
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
    existingOfficeAddress: Map[String, OfficeAddressPartial] = Map.empty
  ) extends OfficeAddressRepositoryAlgebra[IO] {

    def showAllUsers: IO[Map[String, OfficeAddressPartial]] = IO.pure(existingOfficeAddress)

    override def deleteAllByBusinessId(businessId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))

    override def findByOfficeId(officeId: String): IO[Option[OfficeAddressPartial]] = IO.pure(existingOfficeAddress.get(officeId))
    override def create(officeAddressRequest: CreateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(CreateSuccess))

    override def update(officeId: String, request: UpdateOfficeAddressRequest): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(UpdateSuccess))

    override def delete(officeId: String): IO[ValidatedNel[DatabaseErrors, DatabaseSuccess]] = IO(Valid(DeleteSuccess))
  }

  test(".getAddressByBusinessId() - when there is an existing user address details given a user_id should return the correct address details - Right(address)") {

    val existingAddressForUser = testOfficeAddressPartial("business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map("business_1" -> existingAddressForUser))
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.findByOfficeId("business_1")
    } yield expect(result == Right(existingAddressForUser))
  }

  test(".getAddressByBusinessId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testOfficeAddressPartial("business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.findByOfficeId("business_1")
    } yield expect(result == Left(OfficeAddressNotFound))
  }

  test(".createOfficeAddress() - when given a OfficeAddress successfully create the address") {

    val sampleAddress = testAddressRequest("business_1", "office_1")

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map())
    val service = OfficeAddressService(mockOfficeAddressRepository)

    for {
      result <- service.create(sampleAddress)
    } yield expect(result == Valid(CreateSuccess))
  }
}
