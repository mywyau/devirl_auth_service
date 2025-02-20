package services.office

import cats.data.Validated
import cats.data.ValidatedNel
import cats.effect.IO
import cats.implicits.*
import mocks.MockOfficeListingRepository
import models.database.*
import models.office.address_details.OfficeAddressPartial
import models.office.address_details.CreateOfficeAddressRequest

import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.contact_details.CreateOfficeContactDetailsRequest

import models.office.specifications.OfficeSpecifications
import models.office.specifications.OfficeSpecificationsPartial
import models.office.specifications.CreateOfficeSpecificationsRequest
import models.office_listing.OfficeListing
import models.office_listing.OfficeListingCard
import models.office_listing.requests.InitiateOfficeListingRequest
import models.office_listing.requests.OfficeListingRequest
import repositories.office.OfficeListingRepositoryAlgebra
import services.ServiceSpecBase
import services.constants.OfficeListingServiceConstants.*
import services.office.OfficeListingServiceImpl
import testData.TestConstants.*
import weaver.SimpleIOSuite

import java.time.LocalDateTime
import java.time.LocalTime

object OfficeListingServiceSpec extends SimpleIOSuite with ServiceSpecBase {

  test(".initiate() - all repositories succeed") {
    val service = createTestService(
      IO.pure(
        Some(testOfficeListing(businessId1, officeId1))
      ),
      IO.pure(Validated.valid(CreateSuccess))
    )

    val request = testInitiateOfficeListingRequest(businessId1, officeId1)

    val expectedResult =
      OfficeListingCard(
        businessId = businessId1,
        officeId = officeId1,
        officeName = officeName1,
        description = officeDescription1
      )

    for {
      result <- service.initiate(request)
    } yield expect(result == Some(expectedResult))
  }

  // test(".initiate() - one repository fails") {
  //   val service =
  //     createTestService(
  //       IO.pure(None),
  //       IO.pure(Validated.invalidNel(ConstraintViolation))
  //     )

  //   val request = testInitiateOfficeListingRequest(businessId1, officeId1)
  //   val expectedResult = testOfficeListing(businessId1, officeId1)

  //   for {
  //     result <- service.initiate(request)
  //   } yield expect(result == None)
  // }
}
