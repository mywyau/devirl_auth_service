package kafka

import cats.effect.*
import cats.syntax.all.*
import doobie.implicits.*
import doobie.util.fragment
import fs2.kafka.*
import io.circe.syntax.*
import java.time.Instant
import kafka.events.*
import kafka.events.UserRegisteredEvent
import models.outbox.OutboxEvent
import models.Dev
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.*
import scala.concurrent.duration.*
import services.OutboxPublisherServiceImpl
import shared.*
import weaver.*

class OutboxPublisherISpec(global: GlobalRead) extends IOSuite {
  type Res = (KafkaProducerResource, OutboxRepositoryImpl[IO])

  implicit val logger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  private def createOutboxTable(xa: doobie.Transactor[IO]): IO[Unit] =
    sql"""
    CREATE TABLE IF NOT EXISTS outbox_events (
      event_id TEXT PRIMARY KEY,
      aggregate_type TEXT NOT NULL,
      aggregate_id TEXT NOT NULL,
      event_type TEXT NOT NULL,
      payload JSONB NOT NULL,
      published BOOLEAN NOT NULL DEFAULT FALSE,
      retry_count INT NOT NULL DEFAULT 0,
      created_at TIMESTAMP NOT NULL DEFAULT NOW()
    )
  """.update.run.transact(xa).void

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(createOutboxTable(transactor.xa)) 
      producer <- global.getOrFailR[KafkaProducerResource]()
      outboxRepo = new OutboxRepositoryImpl[IO](transactor.xa)
    } yield (producer, outboxRepo)

  private def resetKafkaTopic(topic: String): IO[Unit] =
    IO.blocking {
      import sys.process._
      s"docker exec kafka-container-redpanda-1 rpk topic create $topic --brokers localhost:9092".!
    }.void

  private def deleteTopic(topic: String): IO[Unit] =
    IO.blocking {
      import sys.process._
      s"docker exec kafka-container-redpanda-1 rpk topic delete $topic --brokers localhost:9092".!
    }.void

  test("OutboxPublisher publishes event and marks it as published") { (sharedResource, log) =>

    val kafkaRes = sharedResource._1
    val outboxRepo = sharedResource._2

    val topic = "user.registered.test"

    val event =
      UserRegisteredEvent(
        userId = "user-123",
        username = "Alice",
        email = "alice@example.com",
        userType = Dev,
        createdAt = Instant.now()
      )

    val outbox =
      OutboxEvent
        .from(
          aggregateType = "User",
          aggregateId = event.userId,
          eventType = "UserRegisteredEvent",
          payload = event
        )

    val regProducer =
      new RegistrationEventProducerImpl[IO](topic, kafkaRes.producer)

    val publisher =
      new OutboxPublisherServiceImpl[IO](
        outboxRepo = outboxRepo,
        registrationEventProducer = regProducer,
        topicName = topic
      )

    // Helper: read 1 message from a topic (with a fresh consumer group)
    def readTopicMessages(t: String): IO[List[String]] = {
      val consumerSettings =
        ConsumerSettings[IO, String, String]
          .withBootstrapServers("localhost:9092") // or use your resource value
          .withGroupId(s"outbox-it-test-consumer}")
          .withAutoOffsetReset(AutoOffsetReset.Earliest)

      KafkaConsumer
        .stream(consumerSettings)
        .subscribeTo(t)
        .records
        .map(_.record.value)
        .take(1) // read at least one published record
        .compile
        .toList
        .timeout(5.seconds)
    }

    for {
      _ <- outboxRepo.insert(outbox) // step 1: simulate a pending outbox record
      fiber <- publisher.stream.compile.drain.start // step 2: start the publisher
      _ <- IO.sleep(2.seconds) // give it time to pick up the event
      publishedEvents <- readTopicMessages(topic) // step 3: consume from Kafka
      dbState <- outboxRepo.fetchUnpublished(10) // step 4: check DB state
      _ <- fiber.cancel
    } yield expect(publishedEvents.nonEmpty) and
      expect(dbState.isEmpty)
  }
}
