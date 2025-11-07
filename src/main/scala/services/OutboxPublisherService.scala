package services

import cats.effect.kernel.Async
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
  pollInterval: FiniteDuration = 1.second
) extends OutboxPublisherServiceAlgebra[F] {

  override def stream: Stream[F, Unit] =
    Stream
      .awakeEvery[F](pollInterval)
      .evalMap(_ => outboxRepo.fetchUnpublished(batchSize))
      .flatMap {
        case Nil => Stream.empty
        case list => Stream.emits(list)
      }
      .evalMap(processOne)
      .handleErrorWith { e =>
        Stream.eval(Logger[F].error(e)("[OutboxPublisher] Unexpected error in stream; continuing"))
      }

  def processOne(evt: OutboxEvent): F[Unit] =
    decode[UserRegisteredEvent](evt.payload) match {
      case Left(decErr) =>
        Logger[F].error(
          s"[OutboxPublisher] JSON decode failed for eventId=${evt.eventId}: ${decErr.getMessage}"
        )

      case Right(userEvt) =>
        registrationEventProducer
          .publishUserRegistrationDataCreated(userEvt)
          .flatMap {
            case SuccessfulWrite =>
              outboxRepo.markAsPublished(evt.eventId) *>
                Logger[F].info(
                  s"[OutboxPublisher] Published & marked eventId=${evt.eventId}"
                )

            case FailedWrite(msg, _) =>
              Logger[F].warn(
                s"[OutboxPublisher] Produce failed for eventId=${evt.eventId}: $msg"
              )
            case _ =>
              Logger[F].error(
                s"[OutboxPublisher] Produce failed for eventId=${evt.eventId} - Unknown error"
              )
          }
          .handleErrorWith { e =>
            Logger[F].error(e)(
              s"[OutboxPublisher] Error producing eventId=${evt.eventId}; will retry"
            )
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
