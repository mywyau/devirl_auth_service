package controllers.constants

import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.PrivateDesk
import testData.DeskTestConstants.*
import testData.TestConstants.*

object DeskSpecificationsControllerConstants {

  val testDeskSpecificationsRequest =
    UpdateDeskSpecificationsRequest(
      deskName = "Luxury supreme desk",
      description = Some("Some description"),
      deskType = PrivateDesk,
      quantity = 5,
      rules = Some("Please keep the desk clean and quiet."),
      features = List("Wi-Fi", "Power Outlets", "Monitor", "Ergonomic Chair"),
      openingHours = deskOpeningHours
    )
}
