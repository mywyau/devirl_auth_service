package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class DeleteWorkspaceResponse(response: String)

object DeleteWorkspaceResponse {
  // Manually derive Encoder and Decoder for Workspace
  implicit val workspaceEncoder: Encoder[DeleteWorkspaceResponse] = deriveEncoder[DeleteWorkspaceResponse]
  implicit val workspaceDecoder: Decoder[DeleteWorkspaceResponse] = deriveDecoder[DeleteWorkspaceResponse]
}