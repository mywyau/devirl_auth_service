package models.bookings

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import models.bookings.BookingStatus

import java.time.{LocalDate, LocalDateTime}

case class Booking(
                    id: Option[Int],
                    bookingId: String,
                    bookingName: String,
                    userId: Int,
                    workspaceId: Int,
                    bookingDate: LocalDate,
                    startTime: LocalDateTime,
                    endTime: LocalDateTime,
                    status: BookingStatus,
                    createdAt: LocalDateTime
                  )

object Booking {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[Booking] = deriveEncoder[Booking]
  implicit val bookingDecoder: Decoder[Booking] = deriveDecoder[Booking]
}

