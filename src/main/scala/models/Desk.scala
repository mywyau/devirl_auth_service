package models

import java.time.LocalDateTime

// Desk model
case class Desk(id: String, location: String, status: String)

// Room model
case class Room(id: String, name: String, capacity: Int, status: String)

// User model
case class User(id: String, name: String, email: String)

// Booking model
case class Booking(id: String,
                   userId: String,
                   deskId: Option[String],
                   roomId: Option[String],
                   startTime: LocalDateTime,
                   endTime: LocalDateTime
                  )
