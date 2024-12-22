package controllers.office

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import configuration.models.AppConfig
import controllers.constants.OfficeAddressConstants.*
import controllers.fragments.OfficeAddressRepoFragments.{createOfficeAddressTable, insertOfficeAddressesTable, resetOfficeAddressTable}
import controllers.office.OfficeAddressController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.Json
import io.circe.syntax.*
import models.office.address_details.OfficeAddress
import models.office.adts.*
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
import repositories.office.OfficeAddressRepository
import services.office.address.OfficeAddressService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class OfficeAddressControllerISpec(global: GlobalRead) extends IOSuite {

  implicit val testLogger: SelfAwareStructuredLogger[IO] = Slf4jLogger.getLogger[IO]

  type Res = (TransactorResource, HttpClientResource)

  def sharedResource: Resource[IO, Res] = {
    for {
      transactor <- global.getOrFailR[TransactorResource]()
      _ <- Resource.eval(
        createOfficeAddressTable.update.run.transact(transactor.xa).void *>
          resetOfficeAddressTable.update.run.transact(transactor.xa).void *>
          insertOfficeAddressesTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
    } yield (transactor, client)
  }

  test(
    "GET - /pistachio/business/offices/address//OFF001 - " +
      "given a office_id, find the office address data for given id, returning OK and the address json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](GET, uri"http://127.0.0.1:9999/pistachio/business/offices/address/OFF001")

    val expectedOfficeAddress = testOfficeAddress1(Some(1), "BUS123", "OFF001")

    client.run(request).use { response =>
      response.as[OfficeAddress].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedOfficeAddress
        )
      }
    }
  }

  test(
    "DELETE - /pistachio/business/offices/address/OFF002 - " +
      "given a office_id, delete the office address data for given office id, returning OK and Deleted response json"
  ) { (sharedResources, log) =>

    val transactor = sharedResources._1.xa
    val client = sharedResources._2.client

    val request =
      Request[IO](DELETE, uri"http://127.0.0.1:9999/pistachio/business/offices/address/OFF002")

    val expectedBody = DeletedResponse("Office address deleted successfully")

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
