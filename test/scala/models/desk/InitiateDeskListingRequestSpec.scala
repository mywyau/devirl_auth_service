package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskListing.requests.InitiateDeskListingRequest
import weaver.SimpleIOSuite

object InitiateDeskListingRequestSpec extends SimpleIOSuite {

  val sampleInitiateDeskListingRequest: InitiateDeskListingRequest =
    InitiateDeskListingRequest(
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
