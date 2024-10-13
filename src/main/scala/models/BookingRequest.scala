package models

import java.time.LocalDateTime

// Define BookingRequest
case class BookingRequest(
                           userId: String,
                           deskId: String,
                           roomId: String,
                           startTime: LocalDateTime,
                           endTime: LocalDateTime
                         )
