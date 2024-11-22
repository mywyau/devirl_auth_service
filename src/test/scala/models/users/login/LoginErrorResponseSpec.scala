package models.users.login

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.EncoderOps
import models.responses.ErrorResponse
import models.users.login.adts.{LoginPasswordIncorrect, UsernameNotFound}
import models.users.login.errors.LoginErrorResponse
import weaver.SimpleIOSuite

object LoginErrorResponseSpec extends SimpleIOSuite {

  val sampleLoginErrorResponse: LoginErrorResponse =
    LoginErrorResponse(
      usernameErrors = List(ErrorResponse(UsernameNotFound.code, UsernameNotFound.message)),
      passwordErrors = List(ErrorResponse(LoginPasswordIncorrect.code, LoginPasswordIncorrect.message))
    )

  test("LoginErrorResponse - model encodes correctly to JSON") {

    val jsonResult = sampleLoginErrorResponse.asJson

    val expectedJson =
      s"""{
         |  "usernameErrors" : [
         |     {
         |       "code" : "${UsernameNotFound.code}",
         |       "message" :"${UsernameNotFound.message}"
         |     }
         |  ],
         |  "passwordErrors" : [
         |     {
         |       "code" : "${LoginPasswordIncorrect.code}",
         |       "message" : "${LoginPasswordIncorrect.message}"
         |     }
         |  ]
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
