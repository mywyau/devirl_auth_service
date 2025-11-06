package kafka

import cats.effect.Sync
import cats.syntax.all.*
import fs2.kafka.*
import io.circe.syntax.*
import kafka.events.*
import models.*

trait RegistrationEventProducerAlgebra[F[_]] {

  def publishUserRegistrationDataCreated(event: UserRegisteredEvent): F[KafkaProducerResult]
}

final class RegistrationEventProducerImpl[F[_] : Sync](
  topic: String,
  producer: KafkaProducer[F, String, String]
) extends RegistrationEventProducerAlgebra[F] {

  override def publishUserRegistrationDataCreated(event: UserRegisteredEvent): F[KafkaProducerResult] = {

    val record = ProducerRecord(topic, event.userId, event.asJson.noSpaces)
    val records = ProducerRecords.one(record)

    producer.produce(records).flatten.attempt.map {
      case Right(_) => SuccessfulWrite
      case Left(e) => FailedWrite(e.getMessage)
    }
  }

}
