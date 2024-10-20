package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteWorkspaceResponse(response: String)

object DeleteWorkspaceResponse {
  // Manually derive Encoder and Decoder for Workspace
  implicit val WorkspaceEncoder: Encoder[DeleteWorkspaceResponse] = deriveEncoder[DeleteWorkspaceResponse]
  implicit val WorkspaceDecoder: Decoder[DeleteWorkspaceResponse] = deriveDecoder[DeleteWorkspaceResponse]
}