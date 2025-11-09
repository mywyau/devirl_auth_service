package models.users

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.Dev
import models.ModelsBaseSpec
import models.users.RegistrationData
import weaver.SimpleIOSuite

object RegistrationDataSpec extends SimpleIOSuite with ModelsBaseSpec {

  val testUpdateUserType =
    RegistrationData(
      username = "kaiba",
      email = "kaiba@gmail.com",
      firstName = "bob",
      lastName = "smith",
      userType = Dev
    )

  test("UpdateUserType model encodes correctly to JSON") {

    val jsonResult = testUpdateUserType.asJson

    val expectedJson =
      """
        |{
        |  "username": "kaiba",
        |  "email": "kaiba@gmail.com",
        |  "firstName": "bob",
        |  "lastName": "smith",
        |  "userType": "Dev"
        |}
        |""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    val jsonResultPretty = printer.print(jsonResult)
    val expectedResultPretty = printer.print(expectedResult)

    val differences = jsonDiff(jsonResult, expectedResult, expectedResultPretty, jsonResultPretty)

    for {
      _ <- IO {
        if (differences.nonEmpty) {
          diffPrinter(differences, jsonResultPretty, expectedResultPretty)
        }
      }
    } yield expect(differences.isEmpty)
  }

}
