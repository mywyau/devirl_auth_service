package testData

import models.office.address_details.OfficeAddressPartial

import java.time.LocalDateTime
import java.time.LocalTime

object TestConstants {

  val userId1 = "userId1"
  val userId2 = "userId2"
  val userId3 = "userId3"
  val userId4 = "userId4"
  val userId5 = "userId5"

  val businessId1 = "businessId1"
  val businessId2 = "businessId2"
  val businessId3 = "businessId3"
  val businessId4 = "businessId4"
  val businessId5 = "businessId5"

  val officeId1 = "officeId1"
  val officeId2 = "officeId2"
  val officeId3 = "officeId3"
  val officeId4 = "officeId4"
  val officeId5 = "officeId5"

  val deskId1 = "deskId1"
  val deskId2 = "deskId2"
  val deskId3 = "deskId3"
  val deskId4 = "deskId4"
  val deskId5 = "deskId5"

  val businessName1 = "businessName1"
  val businessName2 = "businessName2"
  val businessName3 = "businessName3"

  val buildingName1 = "butter building"
  val floorNumber1 = "floor 1"
  val floorNumber2 = "floor 2"
  val street1 = "Main street 123"
  val city1 = "New York"
  val country1 = "USA"
  val county1 = "County 123"
  val postcode1 = "123456"
  val latitude1 = 100.1
  val longitude1 = -100.1

  val officeName1 = "Maginificanent Office"

  val officeDescription1 = "some office description"

  val businessDescription1 = "some business description"

  val startTime0900 = LocalTime.of(9, 0, 0)
  val endTime1700 = LocalTime.of(17, 0, 0)

  val createdAt01Jan2025 = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
  val updatedAt01Jan2025 = LocalDateTime.of(2025, 1, 1, 0, 0, 0)

  val noExistingAddresses = Map[String, OfficeAddressPartial]()

  val primaryContactFirstName1 = "Michael"
  val primaryContactLastName1 = "Yau"
  val contactEmail1 = "mike@gmail.com"
  val contactNumber1 = "07402205071"
  val websiteUrl1 = "mikey.com"

}
