package controllers.business

import cats.effect.*
import controllers.ControllerISpecBase
import controllers.constants.BusinessAvailabilityControllerConstants.*
import controllers.fragments.business.BusinessAvailabilityControllerFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.*
import models.Wednesday
import models.business.availability.*
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
import java.time.LocalTime

class BusinessAvailabilityControllerISpec(global: GlobalRead) extends IOSuite with ControllerISpecBase {

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] =
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessAvailabilityTable.update.run.transact(transactor.xa).void *>
          resetBusinessAvailabilityTable.update.run.transact(transactor.xa).void *>
          insertBusinessAvailabilityData.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)

  test(
    "GET - /pistachio/business/businesses/availability/all/businessId1 - " +
      "given a business_id, find the business availability data for given id, returning OK and the availability json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/availability/all/businessId1")

    client.run(request).use { response =>
      response.as[List[RetrieveSingleBusinessAvailability]].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == List(mondayAvailability, tuesdayAvailability, wednesdayAvailability)
        )
      }
    }
  }

  test(
    "PUT - /pistachio/business/availability/opening/hours/update/businessId1 - " +
      "given a business_id, find the business availability data for given id, returning OK and the availability json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val requestBody =
      UpdateBusinessOpeningHoursRequest(
        userId = "userId1",
        businessId = "businessId1",
        day = Monday,
        openingTime = LocalTime.of(12, 0, 0),
        closingTime = LocalTime.of(18, 0, 0)
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/pistachio/business/availability/opening/hours/update/businessId1")
        .withEntity(requestBody.asJson)

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == UpdatedResponse(UpdateSuccess.toString, "Business availability updated successfully")
        )
      }
    }
  }

  test(
    "PUT - /pistachio/business/availability/days/update - " +
      "given a business_id, find the business availability data for given id, returning OK and the availability json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val requestBody =
      UpdateBusinessDaysRequest(
        userId = "userId7",
        businessId = "businessId7",
        days = List(Monday, Tuesday, Wednesday, Thursday)
      )

    val request =
      Request[IO](PUT, uri"http://127.0.0.1:9999/pistachio/business/availability/days/update")
        .withEntity(requestBody.asJson)

    client.run(request).use { response =>
      response.as[UpdatedResponse].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == UpdatedResponse(UpdateSuccess.toString, "Business availability days updated successfully")
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/availability/delete/all/businessId3 - " +
      "given a business_id, delete the business availability details data for given business id, returning OK and Deleted response json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/availability/delete/all/businessId3")

    val expectedBody = DeletedResponse(DeleteSuccess.toString, "Business availability details deleted successfully")

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
