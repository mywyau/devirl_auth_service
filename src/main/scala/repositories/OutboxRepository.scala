package repositories

import cats.effect.MonadCancelThrow
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta
import doobie.implicits.javatime.*
import doobie.postgres.circe.jsonb.implicits.*
import doobie.util.meta.Meta
import doobie.util.transactor.Transactor
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset
import models.outbox.OutboxEvent

trait OutboxRepositoryAlgebra[F[_]] {

  def insert(event: OutboxEvent): F[Int]

}

class OutboxRepositoryImpl[F[_] : MonadCancelThrow](xa: Transactor[F]) extends OutboxRepositoryAlgebra[F] {

  implicit val instantMeta: Meta[Instant] = Meta[OffsetDateTime].imap(_.toInstant)(_.atOffset(ZoneOffset.UTC))

  override def insert(event: OutboxEvent): F[Int] =
    sql"""
      INSERT INTO outbox_events (eventId, aggregate_type, aggregate_id, event_type, payload, created_at, published)
      VALUES (${event.eventId}, ${event.aggregateType}, ${event.aggregateId}, ${event.eventType}, ${event.payload}, ${event.createdAt}, ${event.published})
    """.update.run.transact(xa)
}

object OutboxRepositoryImpl {
  def apply[F[_] : MonadCancelThrow](transactor: Transactor[F]): OutboxRepositoryAlgebra[F] =
    new OutboxRepositoryImpl[F](transactor)
}
