package controllers.constants

import cats.effect.*
import java.time.LocalDateTime
import java.time.LocalTime
import models.business.specifications.CreateBusinessSpecificationsRequest
import models.business.specifications.BusinessSpecifications
import models.business.specifications.BusinessSpecificationsPartial
import testData.BusinessTestConstants.*
import testData.TestConstants.*

object BusinessSpecificationsControllerConstants {

  val testBusinessSpecs: BusinessSpecificationsPartial =
    BusinessSpecificationsPartial(
      userId = "userId1",
      businessId = "businessId1",
      businessName = Some("Example Business Name"),
      description = Some("some description"),
      openingHours = Some(businessOpeningHours1)
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
      openingHours = businessOpeningHours1
    )
}
