package service.workspaces

import cats.effect.IO
import models.workspaces.Workspace
import models.workspaces.errors.WorkspaceNotFound
import repositories.workspaces.WorkspaceRepositoryAlgebra
import services.WorkspaceServiceImpl
import weaver.SimpleIOSuite

import java.time.LocalDateTime

class MockWorkspaceRepository extends WorkspaceRepositoryAlgebra[IO] {

  // Use a mutable Map to store workspaces
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


object WorkspaceServiceSpec extends SimpleIOSuite {

  def freshRepository = new MockWorkspaceRepository

  val sample_workspace1: Workspace =
    Workspace(
      id = Some(1),
      business_id = "BUS123456",
      workspace_id = "WORK12345",
      name = "Downtown Tech Hub",
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

  val sample_workspace2: Workspace =
    Workspace(
      id = Some(2),
      business_id = "BUS123457",
      workspace_id = "WORK12346",
      name = "Creative Collective",
      description = "A vibrant space for artists and creatives to collaborate.",
      address = "456 Art Lane",
      city = "San Francisco",
      country = "USA",
      postcode = "94103",
      price_per_day = BigDecimal(65.00),
      latitude = BigDecimal(37.7749),
      longitude = BigDecimal(-122.4194),
      created_at = LocalDateTime.of(2024, 10, 11, 9, 30)
    )

  test(".createWorkspace() - create a new workspace successfully") {
    val mockRepository = freshRepository
    val workspaceService = new WorkspaceServiceImpl[IO](mockRepository)
    for {
      result <- workspaceService.createWorkspace(sample_workspace1)
    } yield expect(result == Right(1))
  }

  test(".findWorkspaceById() - find a workspace by workspace_id") {
    val mockRepository = freshRepository
    val workspaceService = new WorkspaceServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setWorkspace(sample_workspace1) // Insert the workspace
      result <- workspaceService.findWorkspaceById("WORK12345")
    } yield expect(result == Right(sample_workspace1))
  }

  test(".findWorkspaceById() - return an error if workspace ID does not exist") {
    val mockRepository = freshRepository
    val workspaceService = new WorkspaceServiceImpl[IO](mockRepository)
    for {
      result <- workspaceService.findWorkspaceById("nonexistent_id")
    } yield expect(result == Left(WorkspaceNotFound))
  }

  // Test case for updating a workspace
  test(".updateWorkspace() - update a workspace") {
    val mockRepository = freshRepository
    val workspaceService = new WorkspaceServiceImpl[IO](mockRepository)
    val updatedWorkspace = sample_workspace1.copy(name = "Updated Workspace name")
    for {
      _ <- mockRepository.setWorkspace(sample_workspace1)
      result <- workspaceService.updateWorkspace("WORK12345", updatedWorkspace)
    } yield expect(result == Right(1))
  }

  // Test case for deleting a workspace
  test(".deleteWorkspace() - delete a workspace") {
    val mockRepository = freshRepository
    val workspaceService = new WorkspaceServiceImpl[IO](mockRepository)
    for {
      _ <- mockRepository.setWorkspace(sample_workspace1)
      result <- workspaceService.deleteWorkspace("WORK12345")
      tryToFindWorkspace <- workspaceService.findWorkspaceById("WORK12345")
    } yield expect.all(
      result == Right(1), // delete a single workspace
      tryToFindWorkspace == Left(WorkspaceNotFound)  // return failure value for workspace not found
    )
  }
}
