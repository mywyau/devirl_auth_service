package controllers.constants

import models.desk.deskPricing.UpdateDeskPricingRequest
import testData.DeskTestConstants.*
import testData.TestConstants.*

object DeskPricingControllerConstants {

  val testUpdateRequest =
    UpdateDeskPricingRequest(
      pricePerHour = 15.0,
      pricePerDay = Some(100.0),
      pricePerWeek = Some(600.0),
      pricePerMonth = Some(2000.0),
      pricePerYear = Some(24000.0)
    )

}
