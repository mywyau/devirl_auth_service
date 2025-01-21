package models.desk

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.desk.deskListing.DeskListingCard
import weaver.SimpleIOSuite

object DeskListingCardSpec extends SimpleIOSuite {

  val sampleDeskListingCard: DeskListingCard =
    DeskListingCard(
      deskId = "desk001",
      deskName = "Coffee desk",
      description = "Some desc description"
    )

  test("DeskListingCard model encodes correctly to JSON") {

    val jsonResult = sampleDeskListingCard.asJson

    val expectedJson =
      """
        |{
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
