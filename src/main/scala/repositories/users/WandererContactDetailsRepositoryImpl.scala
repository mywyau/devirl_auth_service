package repositories.users

import cats.Monad
import cats.effect.Concurrent
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.adts.Role
import models.users.wanderer_personal_details.service.WandererContactDetails

import java.sql.Timestamp
import java.time.LocalDateTime


trait WandererContactDetailsRepositoryAlgebra[F[_]] {

  def findByUserId(user_id: String): F[Option[WandererContactDetails]]
  
  def createContactDetails(wandererContactDetails: WandererContactDetails): F[Int]
}

class WandererContactDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends WandererContactDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def findByUserId(userId: String): F[Option[WandererContactDetails]] = {
    val findQuery: F[Option[WandererContactDetails]] =
      sql"SELECT * FROM wanderer_contact_details WHERE user_id = $userId"
        .query[WandererContactDetails]
        .option
        .transact(transactor)
    findQuery
  }

  override def createContactDetails(wandererContactDetails: WandererContactDetails): F[Int] = {
    sql"""
      INSERT INTO wanderer_contact_details (
        user_id,
        contact_number,
        email,
        created_at,
        updated_at
      )
      VALUES (
        ${wandererContactDetails.user_id},
        ${wandererContactDetails.contact_number},
        ${wandererContactDetails.email},
        ${wandererContactDetails.created_at},
        ${wandererContactDetails.updated_at}
        )
    """.update
      .run
      .transact(transactor)
  }

}


object WandererContactDetailsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): WandererContactDetailsRepositoryImpl[F] =
    new WandererContactDetailsRepositoryImpl[F](transactor)
}
