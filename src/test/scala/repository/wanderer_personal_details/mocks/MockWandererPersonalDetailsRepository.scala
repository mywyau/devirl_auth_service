package repository.wanderer_personal_details.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.wanderer_personal_details.service.WandererPersonalDetails
import repositories.users.WandererPersonalDetailsRepositoryAlgebra

case class MockWandererPersonalDetailsRepository(ref: Ref[IO, List[WandererPersonalDetails]]) extends WandererPersonalDetailsRepositoryAlgebra[IO] {

  override def findByUserId(user_id: String): IO[Option[WandererPersonalDetails]] =
    ref.get.map(_.find(_.user_id == user_id))

  override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): IO[Int] =
    ref.modify(contactDetails => (wandererPersonalDetails :: contactDetails, 1))
}