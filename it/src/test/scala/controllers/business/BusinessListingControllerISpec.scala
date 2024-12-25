package controllers.business

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import configuration.models.AppConfig
import controllers.business_listing.BusinessListingController
import controllers.constants.BusinessListingControllerConstants.*
import controllers.fragments.business.BusinessAddressRepoFragments.*
import controllers.fragments.business.BusinessContactDetailsRepoFragments.*
import controllers.fragments.business.BusinessSpecsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.address.BusinessAddress
import models.business.adts.*
import models.business.contact_details.BusinessContactDetails
import models.business.business_listing.requests.BusinessListingRequest
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
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
import repositories.business.{BusinessAddressRepository, BusinessContactDetailsRepository, BusinessSpecificationsRepository}
import services.business.business_listing.BusinessListingService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class BusinessListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

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
    } yield (transactor, client)
  }

  test(
    "POST - /pistachio/business/businesses/listing/create - " +
      "should generate the business listing data for a business in the respective tables, returning Created response"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val businessListingRequest: Json = testBusinessListingRequest("business_id_1").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/businesses/listing/create")
        .withEntity(businessListingRequest)

    val expectedBody = CreatedResponse("Business created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }
}
