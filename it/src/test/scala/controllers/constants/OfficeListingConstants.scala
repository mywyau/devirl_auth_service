package controllers.constants

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.fragments.OfficeAddressRepoFragments.*
import controllers.fragments.OfficeContactDetailsRepoFragments.*
import controllers.fragments.OfficeSpecsRepoFragments.*
import controllers.office_listing.OfficeListingController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.office.adts.*
import models.office.address_details.OfficeAddress
import models.office.contact_details.OfficeContactDetails
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecs}
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.office.{OfficeAddressRepository, OfficeContactDetailsRepository, OfficeSpecsRepository}
import services.office.office_listing.OfficeListingService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

object OfficeListingConstants {

  val testOfficeAvailability: OfficeAvailability =
    OfficeAvailability(
      days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
      startTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      endTime = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeSpecs: OfficeSpecs =
    OfficeSpecs(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability = testOfficeAvailability,
      rules = Some("No smoking. Maintain cleanliness."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  val testOfficeAddress: OfficeAddress =
    OfficeAddress(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      buildingName = Some("OfficeListingControllerISpec Building"),
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

  val testOfficeContactDetails: OfficeContactDetails =
    OfficeContactDetails(
      id = Some(1),
      businessId = "business_id_1",
      officeId = "office_id_1",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


  def testOfficeListingRequest(officeId: String): OfficeListingRequest =
    OfficeListingRequest(
      officeId = officeId,
      addressDetails = testOfficeAddress,
      officeSpecs = testOfficeSpecs,
      contactDetails = testOfficeContactDetails,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
