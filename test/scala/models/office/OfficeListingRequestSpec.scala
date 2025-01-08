package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.office.address_details.requests.CreateOfficeAddressRequest
import models.office.adts.*
import models.office.contact_details.OfficeContactDetails
import models.office.office_listing.requests.OfficeListingRequest
import models.office.specifications.{OfficeAvailability, OfficeSpecifications}
import weaver.SimpleIOSuite
import models.constants.OfficeListingConstants.*

import java.time.{LocalDateTime, LocalTime}

object OfficeListingRequestSpec extends SimpleIOSuite {

  test("OfficeListingRequest model encodes correctly to JSON") {

    val jsonResult = officeListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "officeId": "office_id_1",
        |  "createOfficeSpecificationsRequest": {
        |    "businessId": "business_id_1",
        |    "officeId": "office_id_1",
        |    "officeName": "Modern Workspace",
        |    "description": "A vibrant office space in the heart of the city, ideal for teams or individuals.",
        |    "officeType": "OpenPlanOffice",
        |    "numberOfFloors": 3,
        |    "totalDesks": 3,
        |    "capacity": 50,
        |    "availability": {
        |      "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |      "startTime": "10:00:00",
        |      "endTime": "10:30:00"
        |    },
        |    "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |    "rules": "No smoking. Maintain cleanliness."
        |  },
        |  "createOfficeAddressRequest": {
        |    "businessId": "business_id_1",
        |    "officeId": "office_id_1",
        |    "buildingName": "build_123",
        |    "floorNumber": "floor 1",
        |    "street": "123 Main Street",
        |    "city": "New York",
        |    "country": "USA",
        |    "county": "New York County",
        |    "postcode": "10001",
        |    "latitude": 100.1,
        |    "longitude": -100.1
        |  },
        |  "createOfficeContactDetailsRequest": {
        |    "businessId": "business_id_1",
        |    "officeId": "office_id_1",
        |    "primaryContactFirstName": "Michael",
        |    "primaryContactLastName": "Yau",
        |    "contactEmail": "mike@gmail.com",
        |    "contactNumber": "07402205071"
        |  },
        |  "createdAt": "2025-01-01T00:00:00",
        |  "updatedAt": "2025-01-01T00:00:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
      //      _ <- IO(println(jsonResult))
    } yield {
      expect(jsonResult == expectedResult)
    }
  }

}

