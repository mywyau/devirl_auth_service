package test_data

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime

object ITestConstants {

  val userId1 = "userId1"
  val userId2 = "userId2"
  val userId3 = "userId3"
  val userId4 = "userId4"
  val userId5 = "userId5"

  val clientId1 = "clientId1"
  val clientId2 = "clientId2"
  val clientId3 = "clientId3"
  val clientId4 = "clientId4"
  val clientId5 = "clientId5"

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

  val officeName1 = "Magnificent Office"
  val officeDescription1 = "some office description"
  val businessDescription1 = "some business description"

  val openingTime0900 = LocalTime.of(9, 0, 0)
  val closingTime1700 = LocalTime.of(17, 0, 0)

  val createdAt01Jan2025 = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
  val updatedAt01Jan2025 = LocalDateTime.of(2025, 1, 1, 0, 0, 0)

  val primaryContactFirstName1 = "Michael"
  val primaryContactLastName1 = "Yau"
  val contactEmail1 = "mike@gmail.com"
  val contactNumber1 = "07402205071"
  val websiteUrl1 = "mikey.com"

  val fixed_instant_2025_01_05_0000 = Instant.parse("2025-01-05T00:00:00Z")

}
