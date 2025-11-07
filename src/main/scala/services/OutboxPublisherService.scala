package services

package services.outbox

import cats.effect.kernel.Async
import cats.syntax.all.*
import fs2.kafka.KafkaProducer
import fs2.kafka.ProducerRecord
import fs2.kafka.ProducerRecords
import fs2.kafka.ProducerResult
import fs2.Stream
import io.circe.parser.decode
import io.circe.syntax.*
import models.outbox.OutboxEvent
import org.typelevel.log4cats.Logger
import repositories.OutboxRepositoryAlgebra
import scala.concurrent.duration.*

class OutboxPublisherService[F[_] : Async : Logger](
  outboxRepo: OutboxRepositoryAlgebra[F],
  kafkaProducer: KafkaProducer[F, String, String],
  topicName: String,
  batchSize: Int = 100,
  pollInterval: FiniteDuration = 1.second
) {

  def stream: Stream[F, Unit] =
    Stream.awakeEvery[F](pollInterval) >>
      Stream.eval(outboxRepo.fetchUnpublished(batchSize)).flatMap { events =>
        if (events.isEmpty) Stream.empty
        else Stream.emits(events).evalMap(publishEvent)
      }

  private def publishEvent(event: OutboxEvent): F[Unit] = {
    val record = ProducerRecord(topicName, event.aggregateId, event.payload)
    kafkaProducer.produce(ProducerRecords.one(record)).flatten.attempt.flatMap {
      case Right(_) =>
        outboxRepo.markAsPublished(event.eventId) >>
          Logger[F].info(s"[OutboxPublisher] Published event ${event.eventId} to Kafka.")
      case Left(err) =>
        Logger[F].error(err)(s"[OutboxPublisher] Failed to publish event ${event.eventId}. Will retry later.")
    }
  }
}
