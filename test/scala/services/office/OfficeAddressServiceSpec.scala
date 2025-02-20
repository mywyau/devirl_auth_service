package services.office

import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import cats.syntax.all.*
import java.time.LocalDateTime
import mocks.MockOfficeAddressRepository
import models.database.*
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.UpdateOfficeAddressRequest
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.office.OfficeAddressRepositoryAlgebra
import services.constants.OfficeAddressServiceConstants.*
import services.office.OfficeAddressService
import services.office.OfficeAddressServiceImpl
import services.ServiceSpecBase
import testData.TestConstants.*
import weaver.SimpleIOSuite

object OfficeAddressServiceSpec extends SimpleIOSuite with ServiceSpecBase {

  test(".findByOfficeId() - when there is an existing user address details given a user_id should return the correct address details - Valid(existingAddressForUser)") {

    val existingAddressForUser = testOfficeAddressPartial(businessId1, officeId1)

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(Map(businessId1 -> existingAddressForUser))
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.findByOfficeId(businessId1)
    } yield expect(result == Valid(existingAddressForUser))
  }

  test(".findByOfficeId() - when there are no existing user address details given a user_id should return Left(AddressNotFound)") {

    val existingAddressForUser = testOfficeAddressPartial(businessId1, officeId1)

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(noExistingAddresses)
    val service = new OfficeAddressServiceImpl[IO](mockOfficeAddressRepository)

    for {
      result <- service.findByOfficeId(businessId1)
    } yield expect(result == NotFoundError.invalidNel)
  }

  test(".create() - when given a OfficeAddress successfully create the address") {

    val sampleAddress = testCreateOfficeAddressRequest(businessId1, officeId1)

    val mockOfficeAddressRepository = new MockOfficeAddressRepository(noExistingAddresses)
    val service = OfficeAddressService(mockOfficeAddressRepository)

    for {
      result <- service.create(sampleAddress)
    } yield expect(result == Valid(CreateSuccess))
  }

}
