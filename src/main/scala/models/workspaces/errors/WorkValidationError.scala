package models.workspaces.errors

sealed trait WorkspaceValidationError

case object InvalidWorkspaceId extends WorkspaceValidationError

case object InvalidTimeRange extends WorkspaceValidationError

case object WorkspaceNotFound extends WorkspaceValidationError


