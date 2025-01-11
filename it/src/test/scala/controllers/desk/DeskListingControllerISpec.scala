package controllers.desk

import cats.effect.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import controllers.constants.DeskListingControllerConstants.testDeskListingRequest
import controllers.desk_listing.DeskListingController
import controllers.fragments.DeskListingControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import java.time.LocalDateTime
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.circe.jsonEncoder
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server
import org.http4s.Method.*
import org.typelevel.log4cats.slf4j.Slf4jLogger
import org.typelevel.log4cats.SelfAwareStructuredLogger
import repositories.desk.DeskListingRepositoryImpl
import services.desk_listing.DeskListingServiceImpl
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

class DeskListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createDeskListingsTable.update.run.transact(transactor.xa).void *>
          resetDeskListingTable.update.run.transact(transactor.xa).void *>
          insertDeskListings.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/desk/listing/create - should generate the user profile associated with the user"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/desk/listing/details/create")
        .withEntity(testDeskListingRequest.asJson)

    val expectedDeskListing = CreatedResponse("Business Desk created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == CreatedResponse("Business Desk created successfully")
        )
      }
    }
  }
}
