package repository.constants

import models.office.address_details.OfficeAddress
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.office.office_listing.OfficeListing
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}

import java.time.{LocalDateTime, LocalTime}

object OfficeListingRepoITConstants {

  def officeSpecifications(id: Option[Int], businessId: String, officeId: String): OfficeSpecifications =
    OfficeSpecifications(
      id = id,
      businessId = businessId,
      officeId = officeId,
      officeName = None,
      description = None,
      officeType = None,
      numberOfFloors = None,
      totalDesks = None,
      capacity = None,
      amenities = None,
      availability = None,
      rules = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )

  def officeAddress(id: Option[Int], businessId: String, officeId: String): OfficeAddress =
    OfficeAddress(
      id = id,
      businessId = businessId,
      officeId = officeId,
      buildingName = None,
      floorNumber = None,
      street = None,
      city = None,
      country = None,
      county = None,
      postcode = None,
      latitude = None,
      longitude = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )

  def testContactDetails(id: Option[Int], businessId: String, officeId: String): OfficeContactDetails =
    OfficeContactDetails(
      id = id,
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = None,
      primaryContactLastName = None,
      contactEmail = None,
      contactNumber = None,
      createdAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 12, 0, 0)
    )


  def testOfficeListing(id: Option[Int], businessId: String, officeId: String): OfficeListing = {
    OfficeListing(
      officeId = officeId,
      officeAddressDetails = officeAddress(id, businessId, officeId),
      officeSpecifications = officeSpecifications(id, businessId, officeId),
      officeContactDetails = testContactDetails(id, businessId, officeId)
    )
  }

}
