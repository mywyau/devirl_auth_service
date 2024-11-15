package repository.wanderer_contact_details.mocks

import cats.effect.IO
import cats.effect.kernel.Ref
import models.users.*
import models.users.contact_details.service.WandererContactDetails
import repositories.users.WandererContactDetailsRepositoryAlgebra

case class MockWandererContactDetailsRepository(ref: Ref[IO, List[WandererContactDetails]]) extends WandererContactDetailsRepositoryAlgebra[IO] {

  override def findByUserId(user_id: String): IO[Option[WandererContactDetails]] =
    ref.get.map(_.find(_.user_id == user_id))

  override def createContactDetails(wandererContactDetails: WandererContactDetails): IO[Int] =
    ref.modify(contactDetails => (wandererContactDetails :: contactDetails, 1))
}