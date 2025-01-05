package services.constants

import java.time.LocalDateTime
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial

object BusinessContactDetailsServiceConstants {

  def testCreateBusinessContactDetailsRequest(userId: String, businessId: String): CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = userId,
      businessId = businessId,
      businessName = "MikeyCorp",
      primaryContactFirstName = "Michael",
      primaryContactLastName = "Yau",
      contactEmail = "mike@gmail.com",
      contactNumber = "07402205071",
      websiteUrl = "mikey.com"
    )

  def testContactDetails(userId: String, businessId: String): BusinessContactDetailsPartial =
    BusinessContactDetailsPartial(
      userId = userId,
      businessId = businessId,
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mike@gmail.com"),
      contactNumber = Some("07402205071"),
      websiteUrl = Some("mikey.com")
    )

}
