package controllers.business

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.business.BusinessSpecificationsController
import controllers.constants.BusinessSpecificationsConstants.*
import controllers.fragments.business.BusinessSpecsRepoFragments.*
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.business.adts.*
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
import repositories.business.BusinessSpecificationsRepository
import services.business.specifications.BusinessSpecificationsService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class BusinessSpecificationsControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createBusinessSpecsTable.update.run.transact(transactor.xa).void *>
          resetBusinessSpecsTable.update.run.transact(transactor.xa).void *>
          insertBusinessSpecsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "GET - /pistachio/business/businesses/specifications/business_id_1 - " +
      "given a business_id, find the business specifications data for given id, returning OK and the specifications json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/businesses/specifications/business_id_1")

    val expectedBusinessSpecifications = testBusinessSpecs

    client.run(request).use { response =>
      response.as[BusinessSpecifications].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedBusinessSpecifications
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/businesses/specifications/business_id_2 - " +
      "given a business_id, delete the business specifications data for given business id, returning OK and Deleted response json"
  ) { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/businesses/specifications/business_id_2")

    val expectedBody = DeletedResponse("Business specifications deleted successfully")

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
