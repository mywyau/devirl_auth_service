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
      _ <- global.putR(TransactorResource(xa)) // Store TransactorResource in global context
      //      _ <- Resource.eval(printSchema(xa)) // Print the schema
      //      _ <- Resource.eval(testInsert(xa)) // Print the schema
    } yield ()
  }

  private def printSchema(xa: Transactor[IO]): IO[Unit] = {
    val schemaQuery = sql"""
      SELECT column_name, data_type, is_nullable
      FROM information_schema.columns
      WHERE table_name = 'user_profile'
    """.query[(String, String, String)]
      .to[List]
      .transact(xa)

    schemaQuery.flatMap { schema =>
      IO {
        println("Table Schema for 'user_profile':")
        schema.foreach { case (name, typ, nullable) =>
          println(s"Column: $name, Type: $typ, Nullable: $nullable")
        }
      }
    }
  }


  private def testInsert(xa: Transactor[IO]): IO[Unit] = {
    val insertTest =
      sql"""
      INSERT INTO user_profile (
        userId,
        username,
        password_hash,
        first_name,
        last_name,
        street,
        city,
        country,
        county,
        postcode,
        contact_number,
        email,
        role,
        created_at
      ) VALUES (
        'test_user_id',
        'test_user',
        'hashed_password',
        'First',
        'Last',
        'Street 1',
        'City',
        'Country',
        'County',
        '12345',
        '1234567890',
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
