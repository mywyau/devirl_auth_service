package repository.workspaces

import cats.effect.IO
import models.workspaces.Workspace
import repositories.workspaces.WorkspaceRepositoryAlgebra

class MockWorkspaceRepository extends WorkspaceRepositoryAlgebra[IO] {

  private var workspace: Map[String, Workspace] = Map.empty

  def withInitialWorkspace(initial: Map[String, Workspace]): MockWorkspaceRepository = {
    val repository = new MockWorkspaceRepository
    repository.workspace = initial
    repository
  }

  override def getAllWorkspaces: IO[List[Workspace]] = IO.pure(workspace.values.toList)

  override def findWorkspaceById(workspaceId: String): IO[Option[Workspace]] = IO.pure(workspace.get(workspaceId))

  override def findWorkspaceByName(workspaceName: String): IO[Option[Workspace]] = {
    IO.pure(workspace.values.find(_.name == workspaceName))
  }

  override def setWorkspace(newWorkspace: Workspace): IO[Int] = {
    workspace += (newWorkspace.workspace_id -> newWorkspace)
    IO.pure(1)
  }

  override def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): IO[Int] = {
    if (workspace.contains(workspaceId)) {
      workspace += (workspaceId -> updatedWorkspace)
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

  override def deleteWorkspace(workspaceId: String): IO[Int] = {
    if (workspace.contains(workspaceId)) {
      workspace -= workspaceId
      IO.pure(1)
    } else {
      IO.pure(0)
    }
  }

}
