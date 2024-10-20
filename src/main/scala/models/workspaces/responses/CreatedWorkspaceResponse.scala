package models.workspaces.responses

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class CreatedWorkspaceResponse(response: String)

object CreatedWorkspaceResponse {
  // Manually derive Encoder and Decoder for Booking
  implicit val bookingEncoder: Encoder[CreatedWorkspaceResponse] = deriveEncoder[CreatedWorkspaceResponse]
  implicit val bookingDecoder: Decoder[CreatedWorkspaceResponse] = deriveDecoder[CreatedWorkspaceResponse]
}