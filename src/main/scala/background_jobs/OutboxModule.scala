package modules

import cats.effect.*
import cats.effect.implicits.*
import cats.syntax.all.*
import doobie.hikari.HikariTransactor
import kafka.RegistrationEventProducerAlgebra
import org.typelevel.log4cats.Logger
import repositories.OutboxRepositoryImpl
import services.OutboxPublisherService

import scala.concurrent.duration.*

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
}
