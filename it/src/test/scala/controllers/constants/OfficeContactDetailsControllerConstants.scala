package controllers.constants

import cats.effect.*
import models.office.contact_details.OfficeContactDetails
import models.office.contact_details.requests.CreateOfficeContactDetailsRequest

import java.time.LocalDateTime

object OfficeContactDetailsControllerConstants {

  def createNewContactDetailsRequest(businessId: String, officeId: String): CreateOfficeContactDetailsRequest =
    CreateOfficeContactDetailsRequest(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Ned",
      primaryContactLastName = "Flanders",
      contactEmail = "ned.flanders@example.com",
      contactNumber = "+15551239999"
    )

  def aliceContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Alice",
      primaryContactLastName = "Johnson",
      contactEmail = "alice.johnson@example.com",
      contactNumber = "+15551234567",
      createdAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
    )

  def bobContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Bob",
      primaryContactLastName = "Smith",
      contactEmail = "bob.smith@example.com",
      contactNumber = "+15557654321",
      createdAt = LocalDateTime.of(2023, 2, 1, 15, 30, 0),
      updatedAt = LocalDateTime.of(2023, 2, 1, 15, 30, 0)
    )

  def carolContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = "Carol",
      primaryContactLastName = "Davis",
      contactEmail = "carol.davis@example.com",
      contactNumber = "+15559876543",
      createdAt = LocalDateTime.of(2023, 3, 1, 9, 45, 0),
      updatedAt = LocalDateTime.of(2023, 3, 1, 9, 45, 0)
    )
}
