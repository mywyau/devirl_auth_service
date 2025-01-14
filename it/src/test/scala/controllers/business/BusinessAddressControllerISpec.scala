package controllers.business

import cats.effect.*
import controllers.ControllerISpecBase
import controllers.constants.BusinessAddressControllerConstants.*
import controllers.fragments.business.BusinessAddressRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.address.BusinessAddressPartial
import models.business.address.requests.CreateBusinessAddressRequest
import models.business.address.requests.UpdateBusinessAddressRequest
import models.database.*
import models.responses.CreatedResponse
import models.responses.DeletedResponse
import models.responses.UpdatedResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.implicits.*
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.slf4j.Slf4jLogger
import shared.HttpClientResource
import shared.TransactorResource
import weaver.*

import java.time.LocalDateTime

class BusinessAddressControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessAddressTable.update.run.transact(transactor.xa).void *>
          resetBusinessAddressTable.update.run.transact(transactor.xa).void *>
          insertBusinessAddressTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/businesses/address/details/business_id_1 - " +
      "given a business_id, find the business address data for given id, returning OK and the address json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/businesses/address/details/business_id_1")

    val expectedBusinessAddress = testBusinessAddress("user_id_1", "business_id_1")

    client.run(request).use { response =>
      response.as[BusinessAddressPartial].map { body =>
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

    val expectedBody = CreatedResponse(CreateSuccess.toString, "Business address details created successfully")

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
    "PUT - /pistachio/business/businesses/address/details/update/business_id_4 - " +
      "should update the business address data for a business in database table, returning Updated response"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val updateRequest: UpdateBusinessAddressRequest =
      UpdateBusinessAddressRequest(
        buildingName = Some("Mikey Building"),
        floorNumber = Some("Mikey Floor"),
        street = "Mikey Street",
        city = "Mikey City",
        country = "Mikey Country",
        county = "Mikey County",
        postcode = "CF3 3NJ",
        latitude = 100.1,
        longitude = -100.1
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/pistachio/business/businesses/address/details/update/business_id_4")
        .withEntity(updateRequest.asJson)

    val expectedBody = UpdatedResponse(UpdateSuccess.toString, "Business address updated successfully")

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
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

    val expectedBody = DeletedResponse(DeleteSuccess.toString, "Business address details deleted successfully")

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
