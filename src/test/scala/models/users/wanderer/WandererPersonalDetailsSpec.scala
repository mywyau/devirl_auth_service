package models.users.wanderer

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WandererPersonalDetailsSpec extends SimpleIOSuite {

  val sampleDetails: WandererPersonalDetails =
    WandererPersonalDetails(
      id = Some(1),
      userId = "user_id_1",
      firstName = Some("Mikey"),
      lastName = Some("Smith"),
      contactNumber = Some("07402204444"),
      email = Some("mikey@gmail.com"),
      company = Some("Capgemini"),
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("WandererPersonalDetails - model encodes correctly to JSON") {

    val jsonResult =  sampleDetails.asJson

    val expectedJson =
      """{
        |   "id" : 1,
        |   "userId" : "user_id_1",
        |   "firstName" : "Mikey",
        |   "lastName" : "Smith",
        |   "contactNumber" : "07402204444",
        |   "email" : "mikey@gmail.com",
        |   "company" : "Capgemini",
        |   "createdAt" : "2025-01-01T00:00:00",
        |   "updatedAt" : "2025-01-01T00:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
//      _ <- IO(println(jsonResult.toString))
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }
}
