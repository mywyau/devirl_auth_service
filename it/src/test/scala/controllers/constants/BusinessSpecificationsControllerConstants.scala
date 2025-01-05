package controllers.constants

import cats.effect.*
import models.business.address.BusinessAddress
import models.business.adts.*
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import models.business.specifications.requests.CreateBusinessSpecificationsRequest

import java.time.LocalDateTime
import java.time.LocalTime

object BusinessSpecificationsControllerConstants {

  val testBusinessSpecs: BusinessSpecificationsPartial =
    BusinessSpecificationsPartial(
      userId = "user_id_1",
      businessId = "business_id_1",
      businessName = Some("Example Business Name"),
      description = Some("some description"),
      availability = Some(
        BusinessAvailability(
          days = List("Monday", "Friday"),
          startTime = LocalTime.of(9, 0, 0),
          endTime = LocalTime.of(17, 0, 0)
        )
      )
    )

  def testCreateBusinessSpecificationsRequest(
    userId: String,
    businessId: String
  ): CreateBusinessSpecificationsRequest =
    CreateBusinessSpecificationsRequest(
      userId = userId,
      businessId = businessId,
      businessName = "Example Business Name",
      description = "some description",
      BusinessAvailability(
        days = List("Monday", "Tuesday"),
        startTime = LocalTime.of(10, 0, 0),
        endTime = LocalTime.of(10, 30, 0)
      )
    )
}
