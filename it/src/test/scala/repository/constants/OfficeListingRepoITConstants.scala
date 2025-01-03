package repository.constants

import java.time.LocalDateTime
import java.time.LocalTime
import models.office.address_details.OfficeAddress
import models.office.address_details.OfficeAddressPartial
import models.office.adts.*
import models.office.contact_details.OfficeContactDetailsPartial
import models.office.office_listing.OfficeListing
import models.office.specifications.OfficeAvailability
import models.office.specifications.OfficeSpecificationsPartial

object OfficeListingRepoITConstants {

  def testOfficeAddressPartial(businessId: String, officeId: String): OfficeAddressPartial =
    OfficeAddressPartial(
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
      longitude = None
    )

  def testContactDetailPartial(businessId: String, officeId: String): OfficeContactDetailsPartial =
    OfficeContactDetailsPartial(
      businessId = businessId,
      officeId = officeId,
      primaryContactFirstName = None,
      primaryContactLastName = None,
      contactEmail = None,
      contactNumber = None
    )

  def testOfficeSpecificationsPartial(businessId: String, officeId: String): OfficeSpecificationsPartial =
    OfficeSpecificationsPartial(
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
      rules = None
    )

  def testOfficeListing(id: Option[Int], businessId: String, officeId: String): OfficeListing =
    OfficeListing(
      officeId = officeId,
      officeAddressDetails = testOfficeAddressPartial(businessId, officeId),
      officeSpecifications = testOfficeSpecificationsPartial(businessId, officeId),
      officeContactDetails = testContactDetailPartial(businessId, officeId)
    )

}
