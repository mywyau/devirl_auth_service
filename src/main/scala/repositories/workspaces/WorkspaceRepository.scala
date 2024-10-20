package repositories.workspaces

import cats.effect.Concurrent
import doobie._
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.util.meta.Meta
import models.workspaces.Workspace

import java.sql.{Date, Timestamp}
import java.time.{LocalDate, LocalDateTime}

// Workspace repository algebra defining the interface
trait WorkspaceRepositoryAlgebra[F[_]] {

  def findWorkspaceById(workspaceId: String): F[Option[Workspace]]

  def findWorkspaceByName(workspaceName: String): F[Option[Workspace]]

  def getAllWorkspaces: F[List[Workspace]]

  def setWorkspace(workspace: Workspace): F[Int]

  def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): F[Int]

  def deleteWorkspace(workspaceId: String): F[Int]
}

// Repository implementation for Workspace
class WorkspaceRepository[F[_]: Concurrent](transactor: Transactor[F]) extends WorkspaceRepositoryAlgebra[F] {

  // Implicit Meta instances for mapping LocalDateTime and LocalDate to SQL types
  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val localDateMeta: Meta[LocalDate] =
    Meta[Date].imap(_.toLocalDate)(Date.valueOf)

  // Find a workspace by its ID
  def findWorkspaceById(workspaceId: String): F[Option[Workspace]] =
    sql"""
      SELECT * FROM workspaces
      WHERE workspace_id = $workspaceId
    """
      .query[Workspace]
      .option
      .transact(transactor)

  // Find a workspace by its name
  def findWorkspaceByName(workspaceName: String): F[Option[Workspace]] =
    sql"""
      SELECT * FROM workspaces
      WHERE name = $workspaceName
    """
      .query[Workspace]
      .option
      .transact(transactor)

  // Get all workspaces
  def getAllWorkspaces: F[List[Workspace]] =
    sql"""
      SELECT * FROM workspaces
    """
      .query[Workspace]
      .to[List]
      .transact(transactor)

  // Insert a new workspace into the database
  def setWorkspace(workspace: Workspace): F[Int] =
    sql"""
      INSERT INTO workspaces (
        business_id,
        workspace_id,
        name,
        description,
        address,
        city,
        country,
        postcode,
        price_per_day,
        latitude,
        longitude
      )
      VALUES (
        ${workspace.business_id},
        ${workspace.workspace_id},
        ${workspace.name},
        ${workspace.description},
        ${workspace.address},
        ${workspace.city},
        ${workspace.country},
        ${workspace.postcode},
        ${workspace.price_per_day},
        ${workspace.latitude},
        ${workspace.longitude}
      )
    """
      .update
      .run
      .transact(transactor)

  // Update an existing workspace
  def updateWorkspace(workspaceId: String, updatedWorkspace: Workspace): F[Int] =
    sql"""
      UPDATE workspaces
      SET workspace_id = ${updatedWorkspace.workspace_id},
          name = ${updatedWorkspace.name},
          description = ${updatedWorkspace.description},
          address = ${updatedWorkspace.address},
          city = ${updatedWorkspace.city},
          country = ${updatedWorkspace.country},
          postcode = ${updatedWorkspace.postcode},
          price_per_day = ${updatedWorkspace.price_per_day},
          latitude = ${updatedWorkspace.latitude},
          longitude = ${updatedWorkspace.longitude}
      WHERE workspace_id = $workspaceId
    """
      .update
      .run
      .transact(transactor)

  def deleteWorkspace(workspaceId: String): F[Int] =
    sql"""
      DELETE FROM workspaces
      WHERE workspace_id = $workspaceId
    """
      .update
      .run
      .transact(transactor)
}
