package models.business

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.business.specifications.BusinessAvailability
import models.business.specifications.requests.UpdateBusinessSpecificationsRequest
import weaver.SimpleIOSuite

import java.time.LocalTime

object UpdateBusinessSpecificationsRequestSpec extends SimpleIOSuite {

  val testUpdateBusinessSpecificationsRequest: UpdateBusinessSpecificationsRequest =
    UpdateBusinessSpecificationsRequest(
      businessName = "MikeyCorp",
      description = "Some description",
      availability = BusinessAvailability(
        days = List("Monday", "Tuesday"),
        startTime = LocalTime.of(10, 0, 0),
        endTime = LocalTime.of(10, 30, 0)
      )
    )

  test("UpdateBusinessSpecificationsRequest model encodes correctly to JSON") {

    val jsonResult = testUpdateBusinessSpecificationsRequest.asJson

    val expectedJson =
      """
        |{
        |  "businessName": "MikeyCorp",
        |  "description": "Some description",
        |  "availability": {
        |    "days": ["Monday", "Tuesday"],
        |    "startTime": "10:00:00",
        |    "endTime": "10:30:00"
        |  }
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }

}
