package controllers.workspaces.mocks

import cats.effect.{Concurrent, IO}
import controllers.{WorkspaceController, WorkspaceControllerImpl}
import io.circe.syntax.*
import models.workspaces.Workspace
import models.workspaces.errors.WorkspaceValidationError
import models.workspaces.responses.*
import org.http4s.circe.CirceEntityDecoder.*
import org.http4s.circe.CirceEntityEncoder.*
import org.http4s.implicits.uri
import org.http4s.{Method, Request, Response, Status}
import services.workspaces.algebra.WorkspaceServiceAlgebra
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockWorkspaceService extends WorkspaceServiceAlgebra[IO] {

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