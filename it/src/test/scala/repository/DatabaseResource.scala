package repository

import cats.effect.{IO, Resource}
import doobie.*
import doobie.hikari.HikariTransactor
import doobie.implicits.*
import weaver.{GlobalResource, GlobalWrite}

object DatabaseResource extends GlobalResource {

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool(4)
      xa <- HikariTransactor.newHikariTransactor[IO](
        driverClassName = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5450/cashew_test_db", // Moved to config/env variables later
        user = sys.env.getOrElse("TEST_DB_USER", "cashew_user"), // Default to "postgres"
        pass = sys.env.getOrElse("TEST_DB_PASS", "cashew"), // Default password
        connectEC = ce // Connect execution context (for managing connection pool)
      )
      _ <- global.putR(TransactorResource(xa)) // Store repository.TransactorResource in global context
      //      _ <- Resource.eval(printSchema(xa)) // Print the schema
      //      _ <- Resource.eval(testInsert(xa)) // Print the schema
    } yield ()
  }

  private def printSchema(xa: Transactor[IO]): IO[Unit] = {
    val schemaQuery = sql"""
      SELECT column_name, data_type, is_nullable
      FROM information_schema.columns
      WHERE table_name = 'user_login_details'
    """.query[(String, String, String)]
      .to[List]
      .transact(xa)

    schemaQuery.flatMap { schema =>
      IO {
        println("Table Schema for 'user_login_details':")
        schema.foreach { case (name, typ, nullable) =>
          println(s"Column: $name, Type: $typ, Nullable: $nullable")
        }
      }
    }
  }


  private def testInsert(xa: Transactor[IO]): IO[Unit] = {
    val insertTest =
      sql"""
      INSERT INTO user_login_details (
        user_id,
        username,
        password_hash,
        email,
        role,
        created_at
      ) VALUES (
        'test_user_id',
        'test_user',
        'hashed_password',
        'test@example.com',
        'Wanderer',
        CURRENT_TIMESTAMP
      )
    """.update.run.transact(xa)

    insertTest.attempt.flatMap {
      case Right(_) => IO(println("Test insert succeeded"))
      case Left(e) => IO(println(s"Test insert failed: ${e.getMessage}"))
    }
  }

}
