package repositories

import cats.effect.MonadCancelThrow
import doobie.implicits.javasql.TimestampMeta
import doobie.implicits.javatime.*
import doobie.postgres.circe.jsonb.implicits.*
import doobie.util.meta.Meta
import doobie.util.transactor.Transactor
import models.outbox.OutboxEvent

import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

import doobie.*
import doobie.implicits.*

trait OutboxRepositoryAlgebra[F[_]] {

  def insert(event: OutboxEvent): F[Int]

  def markAsPublished(eventId: String): F[Int]

  def fetchUnpublished(limit: Int = 100): F[List[OutboxEvent]]

}

class OutboxRepositoryImpl[F[_] : MonadCancelThrow](xa: Transactor[F]) extends OutboxRepositoryAlgebra[F] {

  implicit val instantMeta: Meta[Instant] = Meta[OffsetDateTime].imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  override def insert(event: OutboxEvent): F[Int] =
    sql"""
      INSERT INTO outbox_events (eventId, aggregate_type, aggregate_id, event_type, payload, published, created_at)
      VALUES (${event.eventId}, ${event.aggregateType}, ${event.aggregateId}, ${event.eventType}, ${event.payload}, ${event.published}, ${event.createdAt})
    """.update.run.transact(xa)

  override def markAsPublished(eventId: String): F[Int] =
    sql"""
      UPDATE outbox_events SET published = true WHERE id = $eventId
    """.update.run.transact(xa)

  override def fetchUnpublished(limit: Int = 100): F[List[OutboxEvent]] =
    sql"""
      SELECT
        event_id,
        aggregate_type,
        aggregate_id,
        event_type,
        payload,
        published,
        created_at
      FROM outbox_events
      WHERE published = false
      ORDER BY created_at ASC
      LIMIT $limit
    """.query[OutboxEvent].to[List].transact(xa)
}

object OutboxRepository {
  def apply[F[_] : MonadCancelThrow](transactor: Transactor[F]): OutboxRepositoryAlgebra[F] =
    new OutboxRepositoryImpl[F](transactor)
}
