package controllers.business_listing

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.business_listing.BusinessListingController
import controllers.constants.BusinessListingConstants.*
import controllers.fragments.business.BusinessAddressRepoFragments.*
import controllers.fragments.business.BusinessContactDetailsRepoFragments.*
import controllers.fragments.business.BusinessSpecsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.adts.*
import models.business.business_address.service.BusinessAddress
import models.business.business_contact_details.BusinessContactDetails
import models.business.business_listing.requests.BusinessListingRequest
import models.business.business_specs.{BusinessAvailability, BusinessSpecs}
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.{BusinessAddressRepository, BusinessContactDetailsRepository, BusinessSpecsRepository}
import services.business.business_listing.BusinessListingService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class BusinessListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def createServer[F[_] : Async](router: HttpRoutes[F]): Resource[F, Server] =
    EmberServerBuilder
      .default[F]
      .withHost(ipv4"127.0.0.1")
      .withPort(port"9999")
      .withHttpApp(router.orNotFound)
      .build

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessAddressTable.update.run.transact(transactor.xa).void *>
          resetBusinessAddressTable.update.run.transact(transactor.xa).void *>
          createBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
          resetBusinessContactDetailsTable.update.run.transact(transactor.xa).void *>
          createBusinessSpecsTable.update.run.transact(transactor.xa).void *>
          resetBusinessSpecsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val businessAddressRepository = BusinessAddressRepository(transactor)
    val businessContactDetailsRepository = BusinessContactDetailsRepository(transactor)
    val businessSpecsRepository = BusinessSpecsRepository(transactor)

    val businessListingService = BusinessListingService(businessAddressRepository, businessContactDetailsRepository, businessSpecsRepository)
    val businessListingController = BusinessListingController(businessListingService)

    Router(
      "/pistachio" -> businessListingController.routes
    )
  }

  test(
    "POST - /pistachio/business/businesses/listing/create - " +
      "should generate the business listing data for a business in the respective tables, returning Created response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val businessListingRequest: Json = testBusinessListingRequest("business_id_1").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/businesses/listing/create")
        .withEntity(businessListingRequest)

    val expectedBusinessListing = CreatedResponse("Business created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBusinessListing
        )
      }
    }
  }
}
