package repository.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.office.address_details.CreateOfficeAddressRequest
import models.office.address_details.OfficeAddress
import models.office.OpenPlanOffice
import models.office.specifications.CreateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecifications
import testData.TestConstants.*
import testData.OfficeTestConstants.*

object OfficeSpecificationsRepoITConstants {

  def testCreateOfficeSpecificationsRequest(businessId: String, officeId: String): CreateOfficeSpecificationsRequest =
    CreateOfficeSpecificationsRequest(
      businessId = businessId,
      officeId = officeId,
      officeName = "Maginificanent Office",
      description = "some office description",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      openingHours = officeOpeningHours1,
      rules = Some("Please keep the office clean and tidy.")
    )

  def testOfficeSpecs(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications =
    OfficeSpecifications(
      id = Some(1),
      businessId = businessId,
      officeId = officeId,
      officeName = Some("Maginificanent Office"),
      description = Some("some office description"),
      officeType = Some(OpenPlanOffice),
      numberOfFloors = Some(3),
      totalDesks = Some(3),
      capacity = Some(50),
      amenities = Some(List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking")),
      openingHours = Some(officeOpeningHours1),
      rules = Some("Please keep the office clean and tidy."),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

}
