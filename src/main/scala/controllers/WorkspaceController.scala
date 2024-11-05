package controllers

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.syntax.*
import models.workspaces.Workspace
import models.workspaces.errors.*
import models.workspaces.responses.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.Http4sDsl
import services.*
import services.workspaces.algebra.WorkspaceServiceAlgebra

trait WorkspaceController[F[_]] {
  def routes: HttpRoutes[F]
}

object WorkspaceController {
  def apply[F[_] : Concurrent](workspaceService: WorkspaceServiceAlgebra[F]): WorkspaceController[F] =
    new WorkspaceControllerImpl[F](workspaceService)
}

class WorkspaceControllerImpl[F[_] : Concurrent](workspaceService: WorkspaceServiceAlgebra[F]) extends WorkspaceController[F] with Http4sDsl[F] {

  // Create or get JSON decoder/encoder for Workspace object (if needed)
  implicit val workspaceDecoder: EntityDecoder[F, Workspace] = jsonOf[F, Workspace]

  // Define routes for the Workspace Controller
  override val routes: HttpRoutes[F] = HttpRoutes.of[F] {
    // Find workspace by ID
    case GET -> Root / "workspace" / "find" / workspaceId =>
      workspaceService.findWorkspaceById(workspaceId).flatMap {
        case Right(workspace) => Ok(workspace.asJson)
        case Left(WorkspaceNotFound) => NotFound("Workspace not found")
        case Left(InvalidWorkspaceId) => BadRequest("Invalid workspace ID")
        case _ => InternalServerError("An error occurred")
      }

    // Create a new workspace
    case req@POST -> Root / "workspace" / "add" =>
      req.decode[Workspace] { workspace =>
        workspaceService.createWorkspace(workspace).flatMap {
          case Right(_) => Created(CreatedWorkspaceResponse("Workspace created successfully").asJson)
          case _ => InternalServerError(ErrorWorkspaceResponse("An error occurred").asJson)
        }
      }

    // Update an existing workspace by ID
    case req@PUT -> Root / "workspace" / "update" / workspaceId =>
      req.decode[Workspace] { updatedWorkspace =>
        workspaceService.updateWorkspace(workspaceId, updatedWorkspace).flatMap {
          case Right(_) => Ok(UpdatedWorkspaceResponse("Workspace updated successfully").asJson)
          case Left(WorkspaceNotFound) => NotFound(ErrorWorkspaceResponse("Workspace not found").asJson)
          case _ => InternalServerError(ErrorWorkspaceResponse("An error occurred").asJson)
        }
      }

    // Delete a workspace by ID
    case DELETE -> Root / "workspace" / "delete" / workspaceId =>
      workspaceService.deleteWorkspace(workspaceId).flatMap {
        case Right(_) => Ok(DeleteWorkspaceResponse("Workspace deleted successfully").asJson)
        case Left(WorkspaceNotFound) => NotFound(ErrorWorkspaceResponse("Workspace not found").asJson)
        case _ => InternalServerError(ErrorWorkspaceResponse("An error occurred").asJson)
      }
  }
}
