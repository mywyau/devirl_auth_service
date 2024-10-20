package repository.workspaces

import models.workspaces.Workspace
import weaver.SimpleIOSuite

import java.time.LocalDateTime

object WorkspaceRepositorySpec extends SimpleIOSuite {

  def freshRepository = new MockWorkspaceRepository

  val workspace1: Workspace =
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

  val workspace2: Workspace =
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

  val workspace3 =
    Workspace(
      id = Some(3),
      business_id = "BUS123458",
      workspace_id = "WORK12347",
      name = "Urban Cowork",
      description = "An affordable workspace located in the heart of the city.",
      address = "789 Market Street",
      city = "Chicago",
      country = "USA",
      postcode = "60605",
      price_per_day = BigDecimal(50.00),
      latitude = BigDecimal(41.8781),
      longitude = BigDecimal(-87.6298),
      created_at = LocalDateTime.of(2024, 10, 12, 11, 15)
    )

  test(".findWorkspaceById() - find a workspace by it's workspace_id") {
    val mockRepository: MockWorkspaceRepository = freshRepository
    for {
      _ <- mockRepository.setWorkspace(workspace1)
      _ <- mockRepository.setWorkspace(workspace2)
      result <- mockRepository.findWorkspaceById("WORK12345")
    } yield expect(result == Some(workspace1))
  }

  test(".findWorkspaceById() - return an error if workspace ID does not exist") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setWorkspace(workspace1)
      _ <- mockRepository.setWorkspace(workspace2)
      result <- mockRepository.findWorkspaceById("WORK99999")
    } yield expect(result == None)
  }

  test(".setWorkspace() - is able to set a workspace in Repository") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setWorkspace(workspace1)
      _ <- mockRepository.setWorkspace(workspace2)
      workspace_1 <- mockRepository.findWorkspaceById("WORK12345")
      workspace_2 <- mockRepository.findWorkspaceById("WORK12346")
    } yield expect.all(
      workspace_1 == Some(workspace1),
      workspace_2 == Some(workspace2),
    )
  }

  test(".updateWorkspace() - update a workspace") {
    val mockRepository = freshRepository
    val updatedWorkspace = workspace1.copy(name = "Updated Workspace")
    for {
      _ <- mockRepository.setWorkspace(workspace1)
      _ <- mockRepository.setWorkspace(workspace2)
      result <- mockRepository.updateWorkspace(workspaceId = "WORK12345", updatedWorkspace = updatedWorkspace)
      updatedWorkspace <- mockRepository.findWorkspaceById("WORK12345")
    } yield
      expect.all(
        result == 1,
        updatedWorkspace == updatedWorkspace
      )
  }

  test(".deleteWorkspace() - delete a workspace") {
    val mockRepository = freshRepository
    for {
      _ <- mockRepository.setWorkspace(workspace1)
      result <- mockRepository.deleteWorkspace("WORK12345")
      notFoundWorkspace <- mockRepository.findWorkspaceById("WORK12345")
    } yield
      expect.all(
        result == 1,
        notFoundWorkspace.isEmpty
      )
  }
}
