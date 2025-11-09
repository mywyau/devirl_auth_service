package modules

import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.all.*
import doobie.hikari.HikariTransactor
import kafka.RegistrationEventProducerAlgebra
import org.typelevel.log4cats.Logger
import repositories.OutboxRepositoryImpl
import scala.concurrent.duration.*
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import services.OutboxPublisherService

object OutboxModule {

  def make[F[_] : Async : Logger](
    transactor: HikariTransactor[F],
    registrationEventProducer: RegistrationEventProducerAlgebra[F]
  ): Resource[F, Fiber[F, Throwable, Unit]] = {

    val outboxRepo = new OutboxRepositoryImpl[F](transactor)

    val publisher = OutboxPublisherService[F](
      outboxRepo = outboxRepo,
      registrationEventProducer = registrationEventProducer,
      topicName = "user.registered",
      batchSize = 100,
      pollInterval = 1.second
    )

    // Run the outbox stream in background as a fiber
    Resource.eval(
      Logger[F].info("[OutboxModule] Starting outbox publisher background job") *>
        publisher.stream.compile.drain.start
    )
  }

  def parseInt(s: String): Either[String, Int] =
    Either
      .catchOnly[NumberFormatException](s.toInt)
      .leftMap(_ => s"Unable to parse: $s")

}