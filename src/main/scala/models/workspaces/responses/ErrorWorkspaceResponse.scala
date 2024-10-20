package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorWorkspaceResponse(response: String)

object ErrorWorkspaceResponse {
  // Manually derive Encoder and Decoder for Workspace
  implicit val WorkspaceEncoder: Encoder[ErrorWorkspaceResponse] = deriveEncoder[ErrorWorkspaceResponse]
  implicit val WorkspaceDecoder: Decoder[ErrorWorkspaceResponse] = deriveDecoder[ErrorWorkspaceResponse]
}