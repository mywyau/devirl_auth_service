package controllers.business

import cats.effect.*
import com.comcast.ip4s.ipv4
import com.comcast.ip4s.port
import configuration.models.AppConfig
import controllers.business_listing.BusinessListingController
import controllers.constants.BusinessListingControllerConstants.*
import controllers.fragments.business.BusinessAddressRepoFragments.*
import controllers.fragments.business.BusinessContactDetailsRepoFragments.*
import controllers.fragments.business.BusinessSpecificationsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.address.BusinessAddress
import models.business.adts.*
import models.business.business_listing.BusinessListing
import models.business.business_listing.BusinessListingCard
import models.business.business_listing.requests.BusinessListingRequest
import models.business.business_listing.requests.InitiateBusinessListingRequest
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.BusinessAvailability
import models.business.specifications.BusinessSpecifications
import models.responses.CreatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import org.http4s.server.Server
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import repositories.business.BusinessAddressRepository
import repositories.business.BusinessContactDetailsRepository
import repositories.business.BusinessSpecificationsRepository
import services.business.business_listing.BusinessListingService
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

import java.time.LocalDateTime

class BusinessListingControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
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

  test(
    "POST - /pistachio/business/businesses/listing/initiate - " +
      "should generate the business listing data for a business in the respective tables, returning Created response"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val requestBody =
      InitiateBusinessListingRequest(
        userId = "",
        businessId = "BUS9999",
        businessName = "Busines 9999",
        description = "some desc"
      ).asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/businesses/listing/initiate")
        .withEntity(requestBody)

    val expectedBody =
      BusinessListingCard(
        businessId = "BUS9999",
        businessName = "Busines 9999",
        description = "some desc"
      )

    client.run(request).use { response =>
      response.as[BusinessListingCard].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }
}
