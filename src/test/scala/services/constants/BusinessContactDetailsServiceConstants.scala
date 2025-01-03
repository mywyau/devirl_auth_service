package services.constants

import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest

import java.time.LocalDateTime

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

  def testContactDetails(id: Option[Int], userId: String, businessId: String): BusinessContactDetails =
    BusinessContactDetails(
      id = Some(1),
      userId = userId,
      businessId = businessId,
      businessName = Some("MikeyCorp"),
      primaryContactFirstName = Some("Michael"),
      primaryContactLastName = Some("Yau"),
      contactEmail = Some("mike@gmail.com"),
      contactNumber = Some("07402205071"),
      websiteUrl = Some("mikey.com"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )


}
