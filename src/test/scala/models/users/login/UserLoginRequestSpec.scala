package models.users.login

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.authentication.login.requests.UserLoginRequest
import weaver.SimpleIOSuite

object UserLoginRequestSpec extends SimpleIOSuite {

  val sampleUserLoginRequest: UserLoginRequest =
    UserLoginRequest(
      username = "mikey5922",
      password = "cool_password"
    )

  test("UserLoginRequest - model encodes correctly to JSON") {

    val jsonResult = sampleUserLoginRequest.asJson

    val expectedJson =
      """{
        | "username" : "mikey5922",
        | "password" : "cool_password"
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
