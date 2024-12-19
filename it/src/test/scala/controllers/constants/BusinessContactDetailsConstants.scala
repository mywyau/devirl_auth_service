package controllers.constants

import cats.effect.*
import models.business.adts.*
import models.business.business_contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}

import java.time.LocalDateTime

object BusinessContactDetailsConstants {

  def testBusinessContactDetails(id: Option[Int], userId: String, businessId: String): BusinessContactDetails =
    BusinessContactDetails(
      id = id,
      userId = userId,
      businessId = businessId,
      businessName = "Example Business Name",
      primaryContactFirstName = "John",
      primaryContactLastName = "Doe",
      contactEmail = "johndoe@example.com",
      contactNumber = "123-456-7890",
      websiteUrl = "https://example.com",
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )
}
