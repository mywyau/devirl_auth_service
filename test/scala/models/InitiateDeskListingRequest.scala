package models

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import java.time.LocalDateTime
import java.time.LocalTime
import models.desk.deskListing.requests.InitiateDeskListingRequest
import models.desk.deskListing.DeskListing
import models.desk.deskPricing.DeskPricingPartial
import models.desk.deskSpecifications.requests.UpdateDeskSpecificationsRequest
import models.desk.deskSpecifications.Availability
import models.desk.deskSpecifications.DeskSpecificationsPartial
import models.desk.deskSpecifications.PrivateDesk
import weaver.SimpleIOSuite

object InitiateDeskListingRequestSpec extends SimpleIOSuite {

  val sampleInitiateDeskListingRequest: InitiateDeskListingRequest =
    InitiateDeskListingRequest(
      businessId = "business001",
      officeId = "office001",
      deskId = "desk001",
      deskName = "Coffee desk",
      description = "Some desc description"
    )

  test("InitiateDeskListingRequest model encodes correctly to JSON") {

    val jsonResult = sampleInitiateDeskListingRequest.asJson

    val expectedJson =
      """
        |{
        |  "businessId" : "business001",
        |  "officeId" : "office001",
        |  "deskId" : "desk001",
        |  "deskName" : "Coffee desk",
        |  "description" : "Some desc description"
        |}
      """.stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      _ <- IO("")
    } yield expect(jsonResult == expectedResult)
  }
}
