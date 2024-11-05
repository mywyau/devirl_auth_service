package controllers.workspaces

import cats.effect.{Concurrent, IO}
import controllers.workspaces.mocks.MockWorkspaceService
import controllers.{WorkspaceController, WorkspaceControllerImpl}
import io.circe.syntax.*
import models.workspaces.Workspace
import models.workspaces.responses.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.implicits.uri
import org.http4s.{Method, Request, Response, Status}
import services.workspaces.algebra.WorkspaceServiceAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object TestWorkspaceController {
  def apply[F[_] : Concurrent](workspaceService: WorkspaceServiceAlgebra[F]): WorkspaceController[F] =
    new WorkspaceControllerImpl[F](workspaceService)
}

object WorkspaceControllerSpec extends SimpleIOSuite {

  val workspaceService = new MockWorkspaceService

  test("GET - /workspace/find/:workspaceId - should return the workspace when it exists") {

    val controller = WorkspaceController[IO](workspaceService)

    val request = Request[IO](Method.GET, uri"/workspace/find/1")
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

  test("POST - /workspace/add - should create a new workspace and return Created status with body") {
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
      uri = uri"/workspace/add"
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

  test("PUT - /workspace/update/:workspaceId - should update a old workspace and return OK status with body") {

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

    // Create a PUT request with the workspace as JSON
    val request = Request[IO](
      method = Method.PUT,
      uri = uri"/workspace/update/1"
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

  test("DELETE - /workspace/delete/:workspaceId - should update a old workspace and return OK status with body") {

    val workspaceService = new MockWorkspaceService
    val controller = WorkspaceController[IO](workspaceService)

    // Create a DELETE request with the workspace as JSON
    val request = Request[IO](
      method = Method.DELETE,
      uri = uri"/workspace/delete/1"
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
