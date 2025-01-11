package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.office.adts.*
import models.office.specifications.requests.UpdateOfficeSpecificationsRequest
import models.office.specifications.OfficeAvailability
import weaver.SimpleIOSuite

object UpdateOfficeSpecificationsRequestSpec extends SimpleIOSuite {

  val officeSpecifications: UpdateOfficeSpecificationsRequest =
    UpdateOfficeSpecificationsRequest(
      officeName = "Modern Workspace",
      description = "A vibrant office space in the heart of the city, ideal for teams or individuals.",
      officeType = OpenPlanOffice,
      numberOfFloors = 3,
      totalDesks = 3,
      capacity = 50,
      amenities = List("Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"),
      availability = OfficeAvailability(
        days = List("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"),
        startTime = LocalTime.of(10, 0, 0),
        endTime = LocalTime.of(10, 30, 0)
      ),
      rules = Some("No smoking. Maintain cleanliness.")
    )

  test("UpdateOfficeSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = officeSpecifications.asJson

    val expectedJson =
      """
        |{
        |   "officeName": "Modern Workspace",
        |   "description": "A vibrant office space in the heart of the city, ideal for teams or individuals.",
        |   "officeType": "OpenPlanOffice",
        |   "numberOfFloors": 3,
        |   "totalDesks": 3,
        |   "capacity": 50,
        |   "availability": {
        |     "days": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        |     "startTime": "10:00:00",
        |     "endTime": "10:30:00"
        |   },
        |   "amenities": ["Wi-Fi", "Coffee Machine", "Projector", "Whiteboard", "Parking"],
        |   "rules": "No smoking. Maintain cleanliness."
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
