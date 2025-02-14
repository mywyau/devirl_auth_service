package controllers.constants

import cats.effect.*
import java.time.LocalDateTime
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.OfficeContactDetailsPartial
import testData.OfficeTestConstants.*
import testData.TestConstants.*

object OfficeContactDetailsControllerITConstants {

  def createNewContactDetailsRequest(businessId: String, officeId: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Ned",
      primaryContactLastName = "Flanders",
      contactEmail = "ned.flanders@example.com",
      contactNumber = "+15551239999"
    )

  def aliceContactDetails(businessId: String, officeId: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = Some("Alice"),
      primaryContactLastName = Some("Johnson"),
      contactEmail = Some("alice.johnson@example.com"),
      contactNumber = Some("+15551234567")
    )

  def bobContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = Some("Bob"),
      primaryContactLastName = Some("Smith"),
      contactEmail = Some("bob.smith@example.com"),
      contactNumber = Some("+15557654321"),
      createdAt = LocalDateTime.of(2023, 2, 1, 15, 30, 0),
      updatedAt = LocalDateTime.of(2023, 2, 1, 15, 30, 0)
    )

  def carolContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = Some("Carol"),
      primaryContactLastName = Some("Davis"),
      contactEmail = Some("carol.davis@example.com"),
      contactNumber = Some("+15559876543"),
      createdAt = LocalDateTime.of(2023, 3, 1, 9, 45, 0),
      updatedAt = LocalDateTime.of(2023, 3, 1, 9, 45, 0)
    )
}
