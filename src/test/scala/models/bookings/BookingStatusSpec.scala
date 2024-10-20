package models

import cats.effect.IO
import io.circe.Encoder
import io.circe.parser._
import models.bookings.{BookingStatus, Cancelled, Confirmed, Pending}
import weaver.SimpleIOSuite


object BookingStatusSpec extends SimpleIOSuite {

  // Test for encoding BookingStatus to JSON
  test("BookingStatus encodes correctly to JSON") {

    val confirmedJson = Encoder[BookingStatus].apply(Confirmed).noSpaces
    val pendingJson = Encoder[BookingStatus].apply(Pending).noSpaces
    val cancelledJson = Encoder[BookingStatus].apply(Cancelled).noSpaces

    IO(
      expect.all(
        confirmedJson == "\"Confirmed\"",
        pendingJson == "\"Pending\"",
        cancelledJson == "\"Cancelled\""
      )
    )
  }

  // Test for decoding BookingStatus from JSON

  test("BookingStatus decodes correctly from JSON") {

    val confirmedDecoded = decode[BookingStatus]("\"Confirmed\"")
    val pendingDecoded = decode[BookingStatus]("\"Pending\"")
    val cancelledDecoded = decode[BookingStatus]("\"Cancelled\"")

    IO(
      expect.all(
        confirmedDecoded == Right(Confirmed),
        pendingDecoded == Right(Pending),
        cancelledDecoded == Right(Cancelled)
      )
    )
  }

  // Test for invalid status decoding
  test("BookingStatus decoding fails for invalid status") {
    val invalidStatusDecoded = decode[BookingStatus]("\"Unknown\"")

    IO(
      expect(invalidStatusDecoded.isLeft)
    )
  }
}
