package models

import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}
import models.bookings.BookingStatus

import java.time.{LocalDate, LocalDateTime}

case class Booking(
                    id: Option[Int],
                    booking_id: String,
                    booking_name: String,
                    user_id: Int,
                    workspace_id: Int,
                    booking_date: LocalDate,
                    start_time: LocalDateTime,
                    end_time: LocalDateTime,
                    status: BookingStatus,
                    created_at: LocalDateTime
                  )

object Booking {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[Booking] = deriveEncoder[Booking]
  implicit val bookingDecoder: Decoder[Booking] = deriveDecoder[Booking]
}

