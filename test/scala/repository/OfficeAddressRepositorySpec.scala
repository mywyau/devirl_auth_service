package repository

import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.IO
import cats.syntax.all.*
import java.time.LocalDateTime
import mocks.MockOfficeAddressRepository
import models.database.CreateSuccess
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import testData.TestConstants.*
import weaver.SimpleIOSuite

object OfficeAddressRepositorySpec extends SimpleIOSuite {

  def testOfficeAddressRequest(businessId: String, officeId: String): CreateOfficeAddressRequest =
    CreateOfficeAddressRequest(
      businessId = businessId,
      officeId = officeId,
      buildingName = Some("building name"),
      floorNumber = Some("floor 3"),
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("United Kingdom"),
      county = Some("fake County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(-100),
      longitude = Some(-96.7)
    )

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
      businessId = businessId,
      officeId = officeId,
      buildingName = Some("building name"),
      floorNumber = Some("floor 3"),
      street = Some("fake street 1"),
      city = Some("fake city 1"),
      country = Some("United Kingdom"),
      county = Some("fake County"),
      postcode = Some("CF3 3NJ"),
      latitude = Some(-100),
      longitude = Some(-96.7)
    )

  test(".findByOfficeId() - should return an address if officeId exists") {

    val existingAddressForUser = testOfficeAddressPartial(businessId1, officeId1)

    val mockRepo = new MockOfficeAddressRepository(Map(officeId1 -> existingAddressForUser))

    for {
      result <- mockRepo.findByOfficeId(officeId1)
    } yield expect(result == Valid(existingAddressForUser))
  }

  test(".findByOfficeId() - should return None if business_id does not exist") {

    val mockRepo = new MockOfficeAddressRepository(noExistingAddresses)

    for {
      result <- mockRepo.findByOfficeId(officeId1)
    } yield expect(result.isEmpty)
  }

  test(".create() - when given a valid office address should insert the address into the postgres db") {

    val testCreateOfficeAddressRequest: CreateOfficeAddressRequest = testOfficeAddressRequest(businessId2, officeId2)
    val expectedResult: OfficeAddressPartial = testOfficeAddressPartial(businessId2, officeId2)

    val mockRepo = new MockOfficeAddressRepository(noExistingAddresses)

    for {
      result <- mockRepo.create(testCreateOfficeAddressRequest)
      // findInsertedAddress <- mockRepo.findByOfficeId(officeId2)
    } yield expect.all(
      result == Valid(CreateSuccess)
      // findInsertedAddress == Some(expectedResult)
    )
  }
}
