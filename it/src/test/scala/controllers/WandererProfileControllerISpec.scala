package controllers

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.fragments.WandererProfileSqlFragments.*
import controllers.wanderer_profile.WandererProfileController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import models.users.*
import models.users.adts.Wanderer
import models.users.wanderer_profile.errors.{MissingAddress, MissingPersonalDetails, MissingLoginDetails}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, WandererUserProfile}
import models.users.wanderer_profile.responses.error.{ErrorResponse, WandererProfileErrorResponse}
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import repositories.users.{UserLoginDetailsRepositoryImpl, WandererAddressRepository, WandererPersonalDetailsRepository}
import services.password.PasswordServiceImpl
import services.wanderer_profile.WandererProfileService
import shared.{HttpClientResource, TransactorResource}
import weaver.*

import java.time.LocalDateTime

class WandererProfileControllerISpec(global: GlobalRead) extends IOSuite {

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
        createWandererAddressTable.update.run.transact(transactor.xa).void *>
          resetWandererAddressTable.update.run.transact(transactor.xa).void *>
          insertWandererAddressData.update.run.transact(transactor.xa).void *>
          createWandererPersonalDetailsTable.update.run.transact(transactor.xa).void *>
          resetWandererPersonalDetailsTable.update.run.transact(transactor.xa).void *>
          insertWandererPersonalDetailsData.update.run.transact(transactor.xa).void *>
          createWandererLoginDetailsTable.update.run.transact(transactor.xa).void *>
          resetWandererLoginDetailsTable.update.run.transact(transactor.xa).void *>
          insertWandererLoginDetailsTable.update.run.transact(transactor.xa).void
      )
      client <- global.getOrFailR[HttpClientResource]()
      routes = createController(transactor.xa)
      server <- createServer(routes)
    } yield (transactor, client)
  }

  def createController(transactor: Transactor[IO]): HttpRoutes[IO] = {

    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl(transactor)
    val wandererAddressRepository = WandererAddressRepository(transactor)
    val wandererPersonalDetailsRepository = WandererPersonalDetailsRepository(transactor)

    val passwordService = new PasswordServiceImpl

    val wandererUserProfileService =
      WandererProfileService(
        userLoginDetailsRepo = userLoginDetailsRepository,
        wandererAddressRepo = wandererAddressRepository,
        wandererPersonalDetailsRepo = wandererPersonalDetailsRepository,
        passwordService = passwordService
      )

    val wandererUserProfileController = WandererProfileController(wandererUserProfileService)

    Router(
      "/cashew" -> wandererUserProfileController.routes
    )
  }

  test("GET - /cashew/wanderer/user/profile/fake_user_id_1 - should generate the user profile associated with the user") { (transactorResource, log) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request = Request[IO](GET, uri"http://127.0.0.1:9999/cashew/wanderer/user/profile/fake_user_id_1")

    val expectedProfile =
      WandererUserProfile(
        userId = "fake_user_id_1",
        userLoginDetails = Some(
          UserLoginDetails(
            id = Some(1),
            user_id = "fake_user_id_1",
            username = "fake_username",
            password_hash = "hashed_password",
            email = "fake_user_1@example.com",
            role = Wanderer,
            created_at = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
            updated_at = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
          )
        ),
        first_name = Some("bob"),
        last_name = Some("smith"),
        userAddress = Some(
          UserAddress(
            userId = "fake_user_id_1",
            street = "123 Example Street",
            city = "Sample City",
            country = "United Kingdom",
            county = Some("South Glamorgan"),
            postcode = "CF5 3NJ",
            created_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
            updated_at = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
          )
        ),
        contact_number = Some("0123456789"),
        email = Some("fake_user_1@example.com"),
        company = Some("apple"),
        role = Some(Wanderer),
        created_at = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
        updated_at = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
      )

    client.run(request).use { response =>
      response.as[WandererUserProfile].map { body =>
        expect.all(
          response.status == Status.Ok,
          body == expectedProfile
        )
      }
    }
  }

  test("GET - /cashew/wanderer/user/profile/user_id_2 - attempting to get details for the user profile for an unknown user_id should return an WandererProfileErrorResponse") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val request = Request[IO](GET, uri"http://127.0.0.1:9999/cashew/wanderer/user/profile/fake_user_id_2")

    client.run(request).use { response =>
      response.as[WandererProfileErrorResponse].map { body =>
        expect.all(
          response.status == Status.BadRequest,
          body ==
            WandererProfileErrorResponse(
              List(ErrorResponse(MissingLoginDetails.code, MissingLoginDetails.message)),
              List(ErrorResponse(MissingAddress.code, MissingAddress.message)),
              List(ErrorResponse(MissingPersonalDetails.code, MissingPersonalDetails.message)),
              List()
            )
        )
      }
    }
  }

}
