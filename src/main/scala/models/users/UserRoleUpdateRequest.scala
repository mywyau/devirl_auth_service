package models.users

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

case class UserRoleUpdateRequest(
                                  userId: String,
                                  newRole: Role
                                )

object UserRoleUpdateRequest {
  implicit val decoder: Decoder[UserRoleUpdateRequest] = deriveDecoder
}
