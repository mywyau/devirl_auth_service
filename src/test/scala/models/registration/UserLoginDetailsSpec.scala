package models.registration

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.adts.Wanderer
import models.users.registration.profile.UserLoginDetails
import models.users.wanderer.WandererUserProfileSpec.expect
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object UserLoginDetailsSpec extends SimpleIOSuite {

  val sampleUserLoginDetails: UserLoginDetails =
    UserLoginDetails(
      id = Some(1),
      userId = "user_id_1",
      username = "mikey5922",
      passwordHash = "hashed_password_1",
      email = "mikey5922@gmail.com",
      role = Wanderer,
      createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
      updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
    )

  test("UserLoginDetails model encodes correctly to JSON") {

    val jsonResult = sampleUserLoginDetails.asJson

    val expectedJson =
      """
        |{
        |  "id" : 1,
        |  "userId" : "user_id_1",
        |  "username" : "mikey5922",
        |  "passwordHash" : "hashed_password_1",
        |  "email" : "mikey5922@gmail.com",
        |  "role" : "Wanderer",
        |  "createdAt" : "2025-01-01T00:00:00",
        |  "updatedAt" : "2025-01-01T00:00:00"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    for {
      //      _ <- IO(println(jsonResult.toString))
      _ <- IO("")
    } yield {
      expect(jsonResult == expectedResult)
    }
  }
}
