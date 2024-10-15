package models

import java.time.LocalDateTime

// Desk model
case class Desk(id: String, location: String, status: String)

//// Booking model
//case class Booking(id: String,
//                   userId: String,
//                   deskId: Option[String],
//                   roomId: Option[String],
//                   startTime: LocalDateTime,
//                   endTime: LocalDateTime
//                  )
