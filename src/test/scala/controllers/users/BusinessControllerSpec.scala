package controllers.users

import cats.effect.{Concurrent, IO}
import controllers.{BusinessController, BusinessControllerImpl}
import io.circe.syntax._
import models.users.Business
import models.users.errors.BusinessValidationError
import models.users.responses._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Response, Status}
import services.BusinessService
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object TestBusinessController {
  def apply[F[_] : Concurrent](businessService: BusinessService[F]): BusinessController[F] =
    new BusinessControllerImpl[F](businessService)
}

class MockBusinessService extends BusinessService[IO] {

  val sampleBusiness_1: Business =
    Business(
      id = Some(1),
      business_id = "business_1",
      business_name = "Sample Business 1",
      contact_number = "07402205071",
      contact_email = "business_1@gmail.com",
      created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
    )

  override def findBusinessById(businessId: String): IO[Either[BusinessValidationError, Business]] =
    IO.pure(Right(sampleBusiness_1))

  override def createBusiness(business: Business): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))

  override def updateBusiness(businessId: String, business: Business): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))

  override def deleteBusiness(businessId: String): IO[Either[BusinessValidationError, Int]] =
    IO.pure(Right(1))
}

object BusinessControllerSpec extends SimpleIOSuite {

  val businessService = new MockBusinessService

  test("GET /business/:businessId should return the business when it exists") {

    val controller = BusinessController[IO](businessService)

    val request = Request[IO](Method.GET, uri"/business/1")
    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      business <- response.as[Business]
    } yield {
      expect.all(
        response.status == Status.Ok,
        business == businessService.sampleBusiness_1
      )
    }
  }

  test("POST /business should create a new business and return Created status with body") {
    val businessService = new MockBusinessService
    val controller = BusinessController[IO](businessService)

    // Sample business to be sent in POST request
    val newBusiness =
      Business(
        id = Some(2),
        business_id = "business_2",
        business_name = "New Business 2",
        contact_number = "02920362341",
        contact_email = "new_business@gmail.com",
        created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
      )

    // Create a POST request with the business as JSON
    val request = Request[IO](
      method = Method.POST,
      uri = uri"/business"
    ).withEntity(newBusiness.asJson) // Encode business as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[CreatedBusinessResponse]
    } yield {
      expect.all(
        response.status == Status.Created,
        body.response.contains("Business created successfully")
      )
    }
  }

  test("PUT /business/:businessId should update a old business and return OK status with body") {

    val businessService = new MockBusinessService
    val controller = BusinessController[IO](businessService)

    // Sample business to be sent in POST request
    val updatedBusiness =
      Business(
        id = Some(1),
        business_id = "business_1",
        business_name = "Updated Business",
        contact_number = "07402209999",
        contact_email = "updated_business@gmail.com",
        created_at = LocalDateTime.of(2024, 10, 5, 15, 0)
      )

    // Create a POST request with the business as JSON
    val request = Request[IO](
      method = Method.PUT,
      uri = uri"/business/1"
    ).withEntity(updatedBusiness.asJson) // Encode business as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[UpdatedBusinessResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Business updated successfully")
      )
    }
  }

  test("DELETE /business/:businessId should update a old business and return OK status with body") {

    val businessService = new MockBusinessService
    val controller = BusinessController[IO](businessService)

    // Create a POST request with the business as JSON
    val request = Request[IO](
      method = Method.DELETE,
      uri = uri"/business/1"
    ) // Encode business as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[DeleteBusinessResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Business deleted successfully")
      )
    }
  }
}
