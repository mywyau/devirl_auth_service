package models.office

import cats.effect.IO
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import weaver.SimpleIOSuite
import models.office.adts.*
import models.office.adts.OfficeType._
import models.ModelsBaseSpec


object OfficeTypeSpec extends SimpleIOSuite with ModelsBaseSpec {

  test("OfficeType.fromString should return the correct OfficeType for valid strings") {
    for {
      coworkingSpace <- IO(OfficeType.fromString("CoworkingSpace"))
      executiveOffice <- IO(OfficeType.fromString("ExecutiveOffice"))
      meetingRoom <- IO(OfficeType.fromString("MeetingRoom"))
      privateOffice <- IO(OfficeType.fromString("PrivateOffice"))
      openPlanOffice <- IO(OfficeType.fromString("OpenPlanOffice"))
      sharedOffice <- IO(OfficeType.fromString("SharedOffice"))
    } yield expect(coworkingSpace == CoworkingSpace) and
      expect(executiveOffice == ExecutiveOffice) and
      expect(meetingRoom == MeetingRoom) and
      expect(privateOffice == PrivateOffice) and
      expect(openPlanOffice == OpenPlanOffice) and
      expect(sharedOffice == SharedOffice)
  }

  test("OfficeType.fromString should throw an exception for invalid strings") {
    
    val result =
        IO(OfficeType.fromString("UnknownOfficeType"))
           .map(_ => failure("Expected an exception, but got a result"))
           .handleError { ex =>
             expect(ex.getMessage.contains("Unknown office type: UnknownOfficeType"))
           }

    result
  }

  test("OfficeType Encoder should correctly encode OfficeType to JSON") {
  val cases = List(
    CoworkingSpace -> "\"CoworkingSpace\"",
    ExecutiveOffice -> "\"ExecutiveOffice\"",
    MeetingRoom -> "\"MeetingRoom\"",
    PrivateOffice -> "\"PrivateOffice\"",
    OpenPlanOffice -> "\"OpenPlanOffice\"",
    SharedOffice -> "\"SharedOffice\""
  )

  IO {
    cases.map { case (officeType, expectedJson) =>
      val actualJson = OfficeType.officeTypeEncoder(officeType).noSpaces
      expect(actualJson == expectedJson)
    }.reduce(_ and _)
  }
}

  test("OfficeType Decoder should correctly decode JSON to OfficeType") {
    val decodeTests = List(
      "\"CoworkingSpace\"" -> CoworkingSpace,
      "\"ExecutiveOffice\"" -> ExecutiveOffice,
      "\"MeetingRoom\"" -> MeetingRoom,
      "\"PrivateOffice\"" -> PrivateOffice,
      "\"OpenPlanOffice\"" -> OpenPlanOffice,
      "\"SharedOffice\"" -> SharedOffice
    )

    IO {
      decodeTests.map { case (json, expected) =>
        val decoded = decode[OfficeType](json)
        expect(decoded.contains(expected))
      }.reduce(_ and _)
    }
  }

  test("OfficeType Decoder should return an error for invalid JSON values") {
    val invalidJson = "\"UnknownOfficeType\""
    val decodedResult = decode[OfficeType](invalidJson)

    IO {
      expect(decodedResult.isLeft) and
        expect(decodedResult.left.exists(_.getMessage.contains("Invalid office type: UnknownOfficeType")))
    }
  }

  test("OfficeType Decoder should return an error for non-string JSON values") {
    val invalidJson = "123"
    val decodedResult = decode[OfficeType](invalidJson)

    IO {
      expect(decodedResult.isLeft)
    }
  }
}
