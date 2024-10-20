package controllers.workspaces

import cats.effect.{Concurrent, IO}
import controllers.{WorkspaceController, WorkspaceControllerImpl}
import io.circe.syntax._
import models.workspaces.Workspace
import models.workspaces.errors.WorkspaceValidationError
import models.workspaces.responses._
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Method, Request, Response, Status}
import services.WorkspaceService
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object TestWorkspaceController {
  def apply[F[_] : Concurrent](workspaceService: WorkspaceService[F]): WorkspaceController[F] =
    new WorkspaceControllerImpl[F](workspaceService)
}

class MockWorkspaceService extends WorkspaceService[IO] {

  val sample_workspace1: Workspace =
    Workspace(
      id = Some(1),
      business_id = "BUS123456",
      workspace_id = "WORK12345",
      name = "Desk 1",
      description = "A modern coworking space with all amenities for tech startups.",
      address = "123 Main Street",
      city = "New York",
      country = "USA",
      postcode = "10001",
      price_per_day = BigDecimal(75.00),
      latitude = BigDecimal(40.7128),
      longitude = BigDecimal(-74.0060),
      created_at = LocalDateTime.of(2024, 10, 10, 10, 0)
    )

  override def findWorkspaceById(workspaceId: String): IO[Either[WorkspaceValidationError, Workspace]] =
    IO.pure(Right(sample_workspace1))

  override def createWorkspace(workspace: Workspace): IO[Either[WorkspaceValidationError, Int]] =
    IO.pure(Right(1))

  override def updateWorkspace(workspaceId: String, workspace: Workspace): IO[Either[WorkspaceValidationError, Int]] =
    IO.pure(Right(1))

  override def deleteWorkspace(workspaceId: String): IO[Either[WorkspaceValidationError, Int]] =
    IO.pure(Right(1))
}

object WorkspaceControllerSpec extends SimpleIOSuite {

  val workspaceService = new MockWorkspaceService

  test("GET /workspace/:workspaceId should return the workspace when it exists") {

    val controller = WorkspaceController[IO](workspaceService)

    val request = Request[IO](Method.GET, uri"/workspace/1")
    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      workspace <- response.as[Workspace]
    } yield {
      expect.all(
        response.status == Status.Ok,
        workspace == workspaceService.sample_workspace1
      )
    }
  }

  test("POST /workspace should create a new workspace and return Created status with body") {
    val workspaceService = new MockWorkspaceService
    val controller = WorkspaceController[IO](workspaceService)

    // Sample workspace to be sent in POST request
    val newWorkspace =
      Workspace(
        id = Some(1),
        business_id = "BUS123456",
        workspace_id = "WORK12346",
        name = "Desk 2",
        description = "A modern coworking space with all amenities for tech startups.",
        address = "123 Main Street",
        city = "New York",
        country = "USA",
        postcode = "10001",
        price_per_day = BigDecimal(75.00),
        latitude = BigDecimal(40.7128),
        longitude = BigDecimal(-74.0060),
        created_at = LocalDateTime.of(2024, 10, 10, 10, 0)
      )

    // Create a POST request with the workspace as JSON
    val request = Request[IO](
      method = Method.POST,
      uri = uri"/workspace"
    ).withEntity(newWorkspace.asJson) // Encode workspace as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[CreatedWorkspaceResponse]
    } yield {
      expect.all(
        response.status == Status.Created,
        body.response.contains("Workspace created successfully")
      )
    }
  }

  test("PUT /workspace/:workspaceId should update a old workspace and return OK status with body") {

    val workspaceService = new MockWorkspaceService
    val controller = WorkspaceController[IO](workspaceService)

    // Sample workspace to be sent in POST request
    val updatedWorkspace =
      Workspace(
        id = Some(1),
        business_id = "BUS123456",
        workspace_id = "WORK12345",
        name = "Desk 1",
        description = "Updated the description for desk 1",
        address = "123 Main Street",
        city = "New York",
        country = "USA",
        postcode = "10001",
        price_per_day = BigDecimal(75.00),
        latitude = BigDecimal(40.7128),
        longitude = BigDecimal(-74.0060),
        created_at = LocalDateTime.of(2024, 10, 10, 10, 0)
      )

    // Create a POST request with the workspace as JSON
    val request = Request[IO](
      method = Method.PUT,
      uri = uri"/workspace/1"
    ).withEntity(updatedWorkspace.asJson) // Encode workspace as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[UpdatedWorkspaceResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Workspace updated successfully")
      )
    }
  }

  test("DELETE /workspace/:workspaceId should update a old workspace and return OK status with body") {

    val workspaceService = new MockWorkspaceService
    val controller = WorkspaceController[IO](workspaceService)

    // Create a POST request with the workspace as JSON
    val request = Request[IO](
      method = Method.DELETE,
      uri = uri"/workspace/1"
    ) // Encode workspace as JSON for the request body

    val responseIO: IO[Response[IO]] = controller.routes.orNotFound.run(request)

    for {
      response <- responseIO
      body <- response.as[DeleteWorkspaceResponse]
    } yield {
      expect.all(
        response.status == Status.Ok,
        body.response.contains("Workspace deleted successfully")
      )
    }
  }
}
