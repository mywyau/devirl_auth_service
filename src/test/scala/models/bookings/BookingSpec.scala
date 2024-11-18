package models.bookings

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import models.bookings.Booking
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}

object BookingSpec extends SimpleIOSuite {

  val sampleBooking_1: Booking =
    Booking(
      id = Some(1),
      bookingId = "booking_1",
      bookingName = "Sample Booking 1",
      userId = 1,
      workspaceId = 1,
      bookingDate = LocalDate.of(2024, 10, 10),
      startTime = LocalDateTime.of(2024, 10, 10, 9, 0),
      endTime = LocalDateTime.of(2024, 10, 10, 12, 0),
      status = Confirmed,
      createdAt = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  // Test for encoding BookingStatus to JSON
  test("Booking encodes correctly to JSON") {

    val jsonResult = sampleBooking_1.asJson

    val expectedJson =
      """{
        |"id":1,
        |"bookingId":"booking_1",
        |"bookingName":"Sample Booking 1",
        |"userId":1,
        |"workspaceId":1,
        |"bookingDate":"2024-10-10",
        |"startTime":"2024-10-10T09:00:00",
        |"endTime":"2024-10-10T12:00:00",
        |"status":"Confirmed",
        |"createdAt":"2024-10-05T15:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
