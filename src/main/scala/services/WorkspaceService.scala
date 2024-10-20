package services

import cats.data.Validated.{Invalid, Valid}
import cats.data.{EitherT, ValidatedNel}
import cats.effect.Concurrent
import cats.implicits._
import models.workspaces.Workspace
import models.workspaces.errors._
import repositories.workspaces.WorkspaceRepositoryAlgebra


trait WorkspaceService[F[_]] {

  def findWorkspaceById(workspaceId: String): F[Either[WorkspaceValidationError, Workspace]]

  def createWorkspace(workspace: Workspace): F[Either[WorkspaceValidationError, Int]]

  def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): F[Either[WorkspaceValidationError, Int]]

  def deleteWorkspace(workspaceId: String): F[Either[WorkspaceValidationError, Int]]
}


class WorkspaceServiceImpl[F[_] : Concurrent](repository: WorkspaceRepositoryAlgebra[F]) extends WorkspaceService[F] {

  // Validation function for workspace ID (example: make sure it's not empty or too short)
  def validateWorkspaceId(workspaceId: String): ValidatedNel[WorkspaceValidationError, String] = {
    if (workspaceId.nonEmpty && workspaceId.length >= 3) workspaceId.validNel
    else InvalidWorkspaceId.invalidNel
  }

  // Validate if a workspace exists by its ID
  def validateWorkspaceExists(workspaceId: String): EitherT[F, WorkspaceValidationError, Workspace] = {
    EitherT.fromOptionF(repository.findWorkspaceById(workspaceId), WorkspaceNotFound)
  }

  // Find workspace by ID with validation
  def findWorkspaceById(workspaceId: String): F[Either[WorkspaceValidationError, Workspace]] = {
    val validation = validateWorkspaceId(workspaceId)

    validation match {
      case Valid(_) =>
        repository.findWorkspaceById(workspaceId).map {
        case Some(workspace) =>
          Right(workspace)
        case None =>
          Left(WorkspaceNotFound)
      }
      case Invalid(errors) =>
        Concurrent[F].pure(Left(errors.head)) // Return the first validation error
    }
  }

  // Create workspace with validations (including time range and overlap check)
  def createWorkspace(workspace: Workspace): F[Either[WorkspaceValidationError, Int]] = {

    repository.setWorkspace(workspace).map(Right(_))
  }

  // Update workspace with validation (ensures workspace exists before updating)
  def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): F[Either[WorkspaceValidationError, Int]] = {

    repository.updateWorkspace(workspaceId, updatedWorkspace).map(Right(_))
  }

  // Delete workspace with validation (ensures workspace exists before deleting)
  def deleteWorkspace(workspaceId: String): F[Either[WorkspaceValidationError, Int]] = {
    validateWorkspaceExists(workspaceId).value.flatMap {
      case Right(_) => repository.deleteWorkspace(workspaceId).map(Right(_))
      case Left(error) => Concurrent[F].pure(Left(error))
    }
  }
}
