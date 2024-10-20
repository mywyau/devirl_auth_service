package models.bookings

import cats.effect.IO
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import models.Booking
import weaver.SimpleIOSuite

import java.time.{LocalDate, LocalDateTime}

object BookingSpec extends SimpleIOSuite {

  val sampleBooking_1: Booking =
    Booking(
      id = Some(1),
      booking_id = "booking_1",
      booking_name = "Sample Booking 1",
      user_id = 1,
      workspace_id = 1,
      booking_date = LocalDate.of(2024, 10, 10),
      start_time = LocalDateTime.of(2024, 10, 10, 9, 0),
      end_time = LocalDateTime.of(2024, 10, 10, 12, 0),
      status = Confirmed,
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  // Test for encoding BookingStatus to JSON
  test("Booking encodes correctly to JSON") {

    val jsonResult = sampleBooking_1.asJson

    val expectedJson =
      """{
        |"id":1,
        |"booking_id":"booking_1",
        |"booking_name":"Sample Booking 1",
        |"user_id":1,
        |"workspace_id":1,
        |"booking_date":"2024-10-10",
        |"start_time":"2024-10-10T09:00:00",
        |"end_time":"2024-10-10T12:00:00",
        |"status":"Confirmed",
        |"created_at":"2024-10-05T15:00:00"
        |}""".stripMargin

    val expectedResult: Json = parse(expectedJson).getOrElse(Json.Null)

    IO(expect(jsonResult == expectedResult))
  }
}
