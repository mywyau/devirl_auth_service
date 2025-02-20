package testData

import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskListing.DeskListingCard
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskPricing.RetrievedDeskPricing
import models.desk.deskPricing.UpdateDeskPricingRequest
import models.desk.deskSpecifications.UpdateDeskSpecificationsRequest

import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.OpeningHours
import models.desk.deskSpecifications.PrivateDesk
import models.office.address_details.OfficeAddressPartial
import models.Monday
import models.Tuesday
import testData.TestConstants.*

object DeskTestConstants {

  val deskId1 = "deskId1"
  val deskId2 = "deskId2"
  val deskId3 = "deskId3"
  val deskId4 = "deskId4"
  val deskId5 = "deskId5"

  val deskName1 = "Luxury supreme desk"
  val description1 = "Some description"
  val description2 = "Some description"

  val rules = "Please keep the desk clean and quiet."

  val testPricePerHour = 30.00
  val testPricePerDay = 180.00
  val testPricePerWeek = 450.00
  val testPricePerMonth = 1000.00
  val testPricePerYear = 9000.00

  val deskFeatures = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair")

  val sampleDeskListingCard: DeskListingCard =
    DeskListingCard(
      deskId = deskId1,
      deskName = deskName1,
      description = description1
    )

  val deskOpeningHours =
     List(
        OpeningHours(
          day = Monday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        ),
        OpeningHours(
          day = Tuesday,
          openingTime = openingTime0900,
          closingTime = closingTime1700
        )
      )


  val sampleRetrievedDeskPricing: RetrievedDeskPricing =
    RetrievedDeskPricing(
      pricePerHour = Some(testPricePerHour),
      pricePerDay = Some(testPricePerDay),
      pricePerWeek = Some(testPricePerWeek),
      pricePerMonth = Some(testPricePerMonth),
      pricePerYear = Some(testPricePerYear)
    )

  val sampleDeskSpecificationsPartial: DeskSpecificationsPartial =
    DeskSpecificationsPartial(
      deskId = deskId1,
      deskName = deskName1,
      description = Some(description1),
      deskType = Some(PrivateDesk),
      quantity = Some(5),
      features = Some(deskFeatures),
      openingHours = Some(deskOpeningHours),
      rules = Some(rules)
    )

  val sampleDeskListing: DeskListing =
    DeskListing(
      deskId = deskId1,
      sampleDeskSpecificationsPartial,
      sampleRetrievedDeskPricing
    )

  val sampleDeskPricingPartial: DeskPricingPartial =
    DeskPricingPartial(
      pricePerHour = testPricePerHour,
      pricePerDay = Some(testPricePerDay),
      pricePerWeek = Some(testPricePerWeek),
      pricePerMonth = Some(testPricePerMonth),
      pricePerYear = Some(testPricePerYear)
    )

  val sampleInitiateDeskListingRequest: InitiateDeskListingRequest =
    InitiateDeskListingRequest(
      businessId = businessId1,
      officeId = officeId1,
      deskId = deskId1,
      deskName = deskName1,
      description = description1
    )

  val sampleUpdateRequest: UpdateDeskPricingRequest =
    UpdateDeskPricingRequest(
      pricePerHour = testPricePerHour,
      pricePerDay = Some(testPricePerDay),
      pricePerWeek = Some(testPricePerWeek),
      pricePerMonth = Some(testPricePerMonth),
      pricePerYear = Some(testPricePerYear)
    )

  val sampleUpdateRequestMin: UpdateDeskPricingRequest =
    UpdateDeskPricingRequest(
      pricePerHour = testPricePerHour,
      pricePerDay = None,
      pricePerWeek = None,
      pricePerMonth = None,
      pricePerYear = None
    )

  val sampleUpdateDeskSpecificationsRequest: UpdateDeskSpecificationsRequest =
    UpdateDeskSpecificationsRequest(
      deskName = deskName1,
      description = Some(description2),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some(rules),
      features = deskFeatures,
      openingHours = deskOpeningHours
    )
}
