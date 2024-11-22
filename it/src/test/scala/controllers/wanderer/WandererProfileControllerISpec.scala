package controllers.wanderer

import cats.effect.*
import com.comcast.ip4s.{ipv4, port}
import controllers.fragments.WandererProfileSqlFragments.*
import controllers.wanderer_profile.WandererProfileController
import doobie.implicits.*
import doobie.util.transactor.Transactor
import io.circe.syntax.*
import models.responses.ErrorResponse
import models.users.*
import models.users.adts.*
import models.users.wanderer_profile.errors.{MissingAddress, MissingLoginDetails, MissingPersonalDetails}
import models.users.wanderer_profile.profile.{UserAddress, UserLoginDetails, UserPersonalDetails, WandererUserProfile}
import models.users.wanderer_profile.requests.*
import models.users.wanderer_profile.responses.error.WandererProfileErrorResponse
import org.http4s.*
import org.http4s.Method.*
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.{Router, Server}
import repositories.user_profile.{UserLoginDetailsRepositoryImpl, WandererAddressRepository, WandererPersonalDetailsRepository}
import services.authentication.password.PasswordServiceImpl
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
            userId = "fake_user_id_1",
            username = "fake_username",
            passwordHash = "hashed_password",
            email = "fake_user_1@example.com",
            role = Wanderer,
            createdAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
            updatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
          )
        ),
        userPersonalDetails =
          Some(
            UserPersonalDetails(
              userId = "fake_user_id_1",
              firstName = Some("bob"),
              lastName = Some("smith"),
              contactNumber = Some("0123456789"),
              email = Some("fake_user_1@example.com"),
              company = Some("apple"),
              createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
              updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
            )
          ),
        userAddress = Some(
          UserAddress(
            userId = "fake_user_id_1",
            street = Some("123 Example Street"),
            city = Some("Sample City"),
            country = Some("United Kingdom"),
            county = Some("South Glamorgan"),
            postcode = Some("CF5 3NJ"),
            createdAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0),
            updatedAt = LocalDateTime.of(2025, 1, 1, 0, 0, 0)
          )
        ),
        role = Some(Wanderer),
        createdAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0),
        updatedAt = LocalDateTime.of(2023, 1, 1, 12, 0, 0)
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

  test("PUT - /cashew/wanderer/user/profile/fake_user_id_1 - should update the user profile successfully") { (transactorResource, client) =>

    val transactor = transactorResource._1.xa
    val client = transactorResource._2.client

    val updateRequest =
      UpdateProfileRequest(
        loginDetails =
          Some(UpdateLoginDetails(
            username = Some("updated_username"),
            passwordHash = Some("new_hashed_password"),
            email = Some("updated_email@example.com"),
            role = Some(Admin)
          )),
        address =
          Some(UpdateAddress(
            street = Some("456 Updated Street"),
            city = Some("Updated City"),
            country = Some("Updated Country"),
            county = Some("Updated County"),
            postcode = Some("UPDATED123")
          )),
        personalDetails =
          Some(UpdatePersonalDetails(
            firstName = Some("Updated John"),
            lastName = Some("Updated Doe"),
            contactNumber = Some("9876543210"),
            email = Some("updated.john@example.com"),
            company = Some("Updated Corp")
          ))
      )

    val request =
      Request[IO](
        method = Method.PUT,
        uri = uri"http://127.0.0.1:9999/cashew/wanderer/user/profile/fake_user_id_1"
      ).withEntity(updateRequest.asJson)

    client.run(request).use { response =>
      response.as[WandererUserProfile].map { body =>
        expect.all(
          response.status == Status.Ok,
          body.userLoginDetails.exists(_.username == "updated_username"),
          body.userLoginDetails.exists(_.email == "updated_email@example.com"),
          body.userAddress.exists(_.street.contains("456 Updated Street")),
          body.userPersonalDetails.exists(_.contactNumber.contains("9876543210"))
        )
      }
    }
  }
}
