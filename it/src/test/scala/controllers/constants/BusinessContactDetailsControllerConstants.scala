package controllers.constants

import cats.effect.*
import models.business.adts.*
import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.contact_details.requests.CreateBusinessContactDetailsRequest
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications

import java.time.LocalDateTime

object BusinessContactDetailsControllerConstants {

  def testCreateBusinessContactDetailsRequest(userId: String, businessId: String): CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = userId,
      businessId = businessId,
      businessName = "Example Business Name",
      primaryContactFirstName = "John",
      primaryContactLastName = "Doe",
      contactEmail = "johndoe@example.com",
      contactNumber = "123-456-7890",
      websiteUrl = "https://example.com"
    )

  def testBusinessContactDetails(userId: String, businessId: String): BusinessContactDetailsPartial =
    BusinessContactDetailsPartial(
      userId = userId,
      businessId = businessId,
      primaryContactFirstName = Some("John"),
      primaryContactLastName = Some("Doe"),
      contactEmail = Some("johndoe@example.com"),
      contactNumber = Some("123-456-7890"),
      websiteUrl = Some("https://example.com")
    )
}
