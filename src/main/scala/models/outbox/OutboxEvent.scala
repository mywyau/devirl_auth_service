package models.outbox

import io.circe.syntax.*
import io.circe.Encoder
import java.time.Instant
import java.util.UUID

final case class OutboxEvent(
  eventId: String,
  aggregateType: String, // e.g., "User"
  aggregateId: String, // e.g., userId
  eventType: String, // e.g., "UserRegisteredEvent"
  payload: String, // JSON
  published: Boolean = false,
  createdAt: Instant = Instant.now()
)

object OutboxEvent {

  def from[A : Encoder](aggregateType: String, aggregateId: String, eventType: String, payload: A): OutboxEvent =
    OutboxEvent(
      eventId = UUID.randomUUID().toString(),
      aggregateType = aggregateType,
      aggregateId = aggregateId,
      eventType = eventType,
      payload = payload.asJson.noSpaces
    )
}
