package models.users.login

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.users.adts.Wanderer
import models.users.login.responses.LoginResponse
import weaver.SimpleIOSuite

object LoginResponseSpec extends SimpleIOSuite {

  val sampleLoginResponse: LoginResponse =
    LoginResponse(
      userId = "user_id_1",
      username = "mikey5922",
      passwordHash = "hashed_password",
      email = "mikey5922@gmail.com",
      role = Wanderer
    )

  test("LoginResponse - model encodes correctly to JSON") {

    val jsonResult = sampleLoginResponse.asJson

    val expectedJson =
      """{
        | "userId" : "user_id_1",
        | "username" : "mikey5922",
        | "passwordHash" : "hashed_password",
        | "email" : "mikey5922@gmail.com",
        | "role" : "Wanderer"
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
