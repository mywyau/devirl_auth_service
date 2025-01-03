package controllers.business

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.business.BusinessAddressController
import controllers.constants.BusinessAddressControllerConstants.*
import controllers.fragments.business.BusinessAddressRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.address.BusinessAddress
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.adts.*
import models.business.contact_details.BusinessContactDetails
import models.business.specifications.{BusinessAvailability, BusinessSpecifications}
import models.responses.{CreatedResponse, DeletedResponse}
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
import services.business.address.BusinessAddressService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class BusinessAddressControllerISpec(global: GlobalRead) extends IOSuite {

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessAddressTable.update.run.transact(transactor.xa).void *>
          resetBusinessAddressTable.update.run.transact(transactor.xa).void *>
          insertBusinessAddressTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "GET - /pistachio/business/businesses/address/details/business_id_1 - " +
      "given a business_id, find the business address data for given id, returning OK and the address json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/businesses/address/details/business_id_1")

    val expectedBusinessAddress = testBusinessAddress(Some(1), "user_id_1", "business_id_1")

    client.run(request).use { response =>
      response.as[BusinessAddress].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBusinessAddress
        )
      }
    }
  }

  test(
    "POST - /pistachio/business/businesses/address/details/create - " +
      "should generate the business address data for a business in database table, returning Created response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val businessAddressRequest: Json = testBusinessAddressRequest("user_id_3", "business_id_3").asJson

    val request =
      Request[IO](POST, uri"http://127.0.0.1:9999/pistachio/business/businesses/address/details/create")
        .withEntity(businessAddressRequest)

    val expectedBody = CreatedResponse("Business address details created successfully")

    client.run(request).use { response =>
      response.as[CreatedResponse].map { body =>
        expect.all(
          response.status == Status.Created,
          body == expectedBody
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/businesses/address/details/business_id_2 - " +
      "given a business_id, delete the business address details data for given business id, returning OK and Deleted response json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/businesses/address/details/business_id_2")

    val expectedBody = DeletedResponse("Business address details deleted successfully")

    client.run(request).use { response =>
      response.as[DeletedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBody
        )
      }
    }
  }
}
