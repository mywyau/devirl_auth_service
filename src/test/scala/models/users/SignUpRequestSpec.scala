package models.users

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import weaver.SimpleIOSuite

object SignUpRequestSpec extends SimpleIOSuite {

  val signUpRequest1: SignUpRequest =
    SignUpRequest(
      userId = "user_id_1",
      username = "mikey5922",
      password = "hashed_password_1",
      role = Wanderer,
      email = "mikey5922@gmail.com"
    )

  test("SignUpRequest - encodes correctly to JSON") {

    val jsonResult = signUpRequest1.asJson

    val expectedJson =
      """{
        |"userId":"user_id_1",
        |"username":"mikey5922",
        |"password":"hashed_password_1",
        |"role":"Wanderer",
        |"email":"mikey5922@gmail.com"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
