package models

import java.time.{LocalDate, LocalDateTime}


sealed trait BookingStatus
case object Pending extends BookingStatus
case object Confirmed extends BookingStatus
case object Cancelled extends BookingStatus


case class Booking(
                     id: Option[Int],
                     user_id: Int,
                     workspace_id: Int,
                     booking_date: LocalDate,
                     start_time: LocalDateTime,
                     end_time: LocalDateTime,
                     status: BookingStatus,
                     created_at: LocalDateTime
                   )
