import cats.effect.{IO, Resource}
import doobie.*
import doobie.h2.H2Transactor
import doobie.implicits.*
import weaver.{GlobalResource, GlobalWrite}

object DatabaseResource extends GlobalResource {

  def sharedResources(global: GlobalWrite): Resource[IO, Unit] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool(4)
      xa <- H2Transactor.newH2Transactor[IO](
        "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", // In-memory database
        "sa", // Username
        "", // Password
        ce
      )
      _ <- Resource.eval(initializeSchema(xa)) // Set up the database schema once
      _ <- global.putR(TransactorResource(xa)) // Store TransactorResource in global context
      _ <- Resource.eval(printSchema(xa)) // Print the schema
    } yield ()
  }

  private def initializeSchema(xa: Transactor[IO]): IO[Unit] = {
    sql"""
      CREATE TABLE users (
        userId VARCHAR(255) NOT NULL PRIMARY KEY,
        username VARCHAR(255) NOT NULL,
        password_hash TEXT NOT NULL,
        first_name VARCHAR(255) NOT NULL,
        last_name VARCHAR(255) NOT NULL,
        contact_number VARCHAR(100) NOT NULL,
        email VARCHAR(255) NOT NULL,
        role VARCHAR(50) NOT NULL DEFAULT 'Wanderer',
        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
      )
    """.update.run.transact(xa).void
  }


  private def printSchema(xa: Transactor[IO]): IO[Unit] = {
    val schemaQuery = sql"""
      SELECT COLUMN_NAME, TYPE_NAME, IS_NULLABLE
      FROM INFORMATION_SCHEMA.COLUMNS
      WHERE TABLE_NAME = 'USERS'
    """.query[(String, String, String)]
      .to[List]
      .transact(xa)

    schemaQuery.flatMap { schema =>
      IO {
        println("Table Schema for 'users':")
        schema.foreach { case (name, typ, nullable) =>
          println(s"Column: $name, Type: $typ, Nullable: $nullable")
        }
      }
    }
  }

}
