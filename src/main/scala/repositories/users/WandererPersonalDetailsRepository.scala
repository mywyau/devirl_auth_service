package repositories.users

import cats.Monad
import cats.effect.Concurrent
import cats.syntax.all.*
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.*
import doobie.util.meta.Meta
import models.users.adts.Role
import models.users.wanderer_personal_details.service.WandererPersonalDetails

import java.sql.Timestamp
import java.time.LocalDateTime


trait WandererPersonalDetailsRepositoryAlgebra[F[_]] {

  def findByUserId(userId: String): F[Option[WandererPersonalDetails]]

  def createRegistrationPersonalDetails(userId: String): F[Int]

  def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): F[Int]

  def updatePersonalDetailsDynamic(
                                    userId: String,
                                    firstName: Option[String],
                                    lastName: Option[String],
                                    contactNumber: Option[String],
                                    email: Option[String],
                                    company: Option[String]
                                  ): F[Option[WandererPersonalDetails]]
}

class WandererPersonalDetailsRepositoryImpl[F[_] : Concurrent : Monad](transactor: Transactor[F]) extends WandererPersonalDetailsRepositoryAlgebra[F] {

  implicit val localDateTimeMeta: Meta[LocalDateTime] =
    Meta[Timestamp].imap(_.toLocalDateTime)(Timestamp.valueOf)

  implicit val roleMeta: Meta[Role] = Meta[String].imap(Role.fromString)(_.toString)

  override def findByUserId(userId: String): F[Option[WandererPersonalDetails]] = {
    val findQuery: F[Option[WandererPersonalDetails]] =
      sql"SELECT * FROM wanderer_personal_details WHERE user_id = $userId"
        .query[WandererPersonalDetails]
        .option
        .transact(transactor)
    findQuery
  }

  def createRegistrationPersonalDetails(userId: String): F[Int] = {
    sql"""
        INSERT INTO wanderer_personal_details (
          user_id
        ) VALUES (
          $userId
          )
      """.update
      .run
      .transact(transactor)
  }

  override def createPersonalDetails(wandererPersonalDetails: WandererPersonalDetails): F[Int] = {
    sql"""
        INSERT INTO wanderer_personal_details (
          user_id,
          first_name,
          last_name,
          contact_number,
          email,
          company,
          created_at,
          updated_at
        ) VALUES (
          ${wandererPersonalDetails.userId},
          ${wandererPersonalDetails.firstName},
          ${wandererPersonalDetails.lastName},
          ${wandererPersonalDetails.contactNumber},
          ${wandererPersonalDetails.email},
          ${wandererPersonalDetails.company},
          ${wandererPersonalDetails.createdAt},
          ${wandererPersonalDetails.updatedAt}
          )
      """.update
      .run
      .transact(transactor)
  }


  override def updatePersonalDetailsDynamic(
                                             userId: String,
                                             firstName: Option[String],
                                             lastName: Option[String],
                                             contactNumber: Option[String],
                                             email: Option[String],
                                             company: Option[String]
                                           ): F[Option[WandererPersonalDetails]] = {

    // Dynamically build the update query
    val updates = List(
      firstName.map(fn => fr"first_name = $fn"),
      lastName.map(ln => fr"last_name = $ln"),
      contactNumber.map(cn => fr"contact_number = $cn"),
      email.map(e => fr"email = $e"),
      company.map(c => fr"company = $c")
    ).flatten

    val updateQuery: Option[ConnectionIO[Int]] =
      if (updates.nonEmpty) {
        (fr"UPDATE wanderer_personal_details SET" ++ updates.intercalate(fr",") ++
          fr"WHERE user_id = $userId").update.run.some
      } else None

    val selectQuery: ConnectionIO[Option[WandererPersonalDetails]] =
      sql"""
          SELECT id, user_id, first_name, last_name, contact_number, email, company, created_at, updated_at
          FROM wanderer_personal_details
          WHERE user_id = $userId
        """.query[WandererPersonalDetails].option

    val result: ConnectionIO[Option[WandererPersonalDetails]] = updateQuery match {
      case Some(query) =>
        for {
          rowsAffected <- query
          updatedDetails <- if (rowsAffected > 0) selectQuery else none[WandererPersonalDetails].pure[ConnectionIO]
        } yield updatedDetails
      case None =>
        selectQuery // If no updates, just return the existing personal details
    }

    result.transact(transactor)
  }
}


object WandererPersonalDetailsRepository {
  def apply[F[_] : Concurrent : Monad](
                                        transactor: Transactor[F]
                                      ): WandererPersonalDetailsRepositoryImpl[F] =
    new WandererPersonalDetailsRepositoryImpl[F](transactor)
}
