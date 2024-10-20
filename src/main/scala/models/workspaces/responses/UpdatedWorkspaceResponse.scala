package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class UpdatedWorkspaceResponse(response: String)


object UpdatedWorkspaceResponse {
  // Manually derive Encoder and Decoder for Workspace
  implicit val WorkspaceEncoder: Encoder[UpdatedWorkspaceResponse] = deriveEncoder[UpdatedWorkspaceResponse]
  implicit val WorkspaceDecoder: Decoder[UpdatedWorkspaceResponse] = deriveDecoder[UpdatedWorkspaceResponse]
}

