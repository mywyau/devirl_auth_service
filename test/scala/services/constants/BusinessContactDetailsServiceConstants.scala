package services.constants

import models.business.contact_details.BusinessContactDetails
import models.business.contact_details.BusinessContactDetailsPartial
import models.business.contact_details.CreateBusinessContactDetailsRequest
import testData.TestConstants.*

import java.time.LocalDateTime

object BusinessContactDetailsServiceConstants {

  def testCreateBusinessContactDetailsRequest(userId: String, businessId: String): CreateBusinessContactDetailsRequest =
    CreateBusinessContactDetailsRequest(
      userId = userId,
      businessId = businessId,
      primaryContactFirstName = primaryContactFirstName1,
      primaryContactLastName = primaryContactLastName1,
      contactEmail = contactEmail1,
      contactNumber = contactNumber1,
      websiteUrl = websiteUrl1
    )

  def testContactDetails(userId: String, businessId: String): BusinessContactDetailsPartial =
    BusinessContactDetailsPartial(
      userId = userId,
      businessId = businessId,
      primaryContactFirstName = Some(primaryContactFirstName1),
      primaryContactLastName = Some(primaryContactLastName1),
      contactEmail = Some(contactEmail1),
      contactNumber = Some(contactNumber1),
      websiteUrl = Some(websiteUrl1)
    )

}
