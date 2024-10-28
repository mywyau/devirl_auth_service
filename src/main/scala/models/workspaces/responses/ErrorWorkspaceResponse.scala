package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class ErrorWorkspaceResponse(response: String)

object ErrorWorkspaceResponse {
  // Manually derive Encoder and Decoder for Workspace
  implicit val workspaceEncoder: Encoder[ErrorWorkspaceResponse] = deriveEncoder[ErrorWorkspaceResponse]
  implicit val workspaceDecoder: Decoder[ErrorWorkspaceResponse] = deriveDecoder[ErrorWorkspaceResponse]
}