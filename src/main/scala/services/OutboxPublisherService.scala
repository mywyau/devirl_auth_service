package services

import cats.effect.kernel.{Async, Temporal}
import cats.syntax.all.*
import fs2.Stream
import io.circe.parser.decode
import kafka.*
import kafka.events.UserRegisteredEvent
import models.outbox.OutboxEvent
import org.typelevel.log4cats.Logger
import repositories.OutboxRepositoryAlgebra
import scala.concurrent.duration.*

trait OutboxPublisherServiceAlgebra[F[_]] {
  def stream: Stream[F, Unit]
}

class OutboxPublisherServiceImpl[F[_] : Async : Logger](
  outboxRepo: OutboxRepositoryAlgebra[F],
  registrationEventProducer: RegistrationEventProducerAlgebra[F],
  topicName: String,
  batchSize: Int = 100,
  pollInterval: FiniteDuration = 1.second,
  maxRetries: Int = 10
) extends OutboxPublisherServiceAlgebra[F] {

  override def stream: Stream[F, Unit] =
    Stream
      .awakeEvery[F](pollInterval)
      .evalMap(_ => outboxRepo.fetchUnpublished(batchSize))
      .flatMap {
        case Nil  => Stream.empty
        case list => Stream.emits(list)
      }
      .evalMap(evt =>
        processOne(evt).handleErrorWith(e =>
          Logger[F].error(e)(s"[OutboxPublisher] Error in processOne(${evt.eventId})")
        )
      )
      .handleErrorWith { e =>
        Stream.eval(Logger[F].error(e)("[OutboxPublisher] Unexpected stream error; continuing"))
      }

  /** ✅ Always return F[Unit] — this method is all side effects */
  private def processOne(evt: OutboxEvent): F[Unit] =
    val jsonStr = evt.payload.noSpaces
    decode[UserRegisteredEvent](jsonStr) match {
      case Left(decErr) =>
        Logger[F].error(
          s"[OutboxPublisher] JSON decode failed for eventId=${evt.eventId}: ${decErr.getMessage}"
        ) *>
          outboxRepo.incrementRetryCount(evt.eventId, decErr.getMessage).void

      case Right(userEvt) =>
        registrationEventProducer
          .publishUserRegistrationDataCreated(userEvt)
          .flatMap {
            case SuccessfulWrite =>
              outboxRepo
                .markAsPublished(evt.eventId)
                .void *>
                Logger[F].info(s"[OutboxPublisher] ✅ Published & marked eventId=${evt.eventId}")

            case FailedWrite(msg, _) =>
              val backoff = (evt.retryCount + 1).seconds.min(1.minute)
              Logger[F].warn(
                s"[OutboxPublisher] Produce failed for eventId=${evt.eventId}: $msg — retrying in $backoff"
              ) *>
                Temporal[F].sleep(backoff) *>
                outboxRepo.incrementRetryCount(evt.eventId, msg).void

            case _ =>
              Logger[F].error(
                s"[OutboxPublisher] Produce failed for eventId=${evt.eventId} — Unknown error"
              ) *>
                outboxRepo.incrementRetryCount(evt.eventId, "Unknown error").void
          }
    }
}

object OutboxPublisherService {
  def apply[F[_] : Async : Logger](
    outboxRepo: OutboxRepositoryAlgebra[F],
    registrationEventProducer: RegistrationEventProducerAlgebra[F],
    topicName: String,
    batchSize: Int = 100,
    pollInterval: FiniteDuration = 1.second
  ): OutboxPublisherServiceAlgebra[F] =
    new OutboxPublisherServiceImpl[F](
      outboxRepo,
      registrationEventProducer,
      topicName,
      batchSize,
      pollInterval
    )
}
