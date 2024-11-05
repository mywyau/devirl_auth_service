package services.workspaces.algebra

import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits._
import models.workspaces.Workspace
import models.workspaces.errors._
import repositories.workspaces.WorkspaceRepositoryAlgebra


trait WorkspaceServiceAlgebra[F[_]] {

  def findWorkspaceById(workspaceId: String): F[Either[WorkspaceValidationError, Workspace]]

  def createWorkspace(workspace: Workspace): F[Either[WorkspaceValidationError, Int]]

  def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): F[Either[WorkspaceValidationError, Int]]

  def deleteWorkspace(workspaceId: String): F[Either[WorkspaceValidationError, Int]]
}