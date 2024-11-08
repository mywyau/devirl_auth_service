package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import dev.profunktor.redis4cats.effect.Log
import dev.profunktor.redis4cats.{Redis, RedisCommands}
import doobie.hikari.HikariTransactor
import models.users.database.UserLoginDetails
import org.http4s.HttpRoutes
import repositories.*
import repositories.bookings.BookingRepository
import repositories.business.BusinessRepository
import repositories.users.{UserLoginDetailsRepositoryImpl, UserProfileRepositoryImpl}
import repositories.workspaces.WorkspaceRepository
import services.*
import services.auth.{AuthenticationServiceImpl, RedisTokenCommands, RegistrationServiceImpl, TokenServiceImpl}
import services.bookings.BookingServiceImpl
import services.business.BusinessServiceImpl

import java.time.Clock

object Routes {

  def createAuthRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): Resource[F, HttpRoutes[F]] = {

    // Define required dependencies
    val secretKey = "your-secret-key" // Ideally from secure config
    val clock = Clock.systemUTC()

    // Set up Redis commands (adjust "redis://localhost:6379" as per your Redis configuration)
    val redisResource: Resource[F, RedisCommands[F, String, String]] = Redis[F].utf8("redis://localhost:6379")

    redisResource.map { redisCommands =>
      val userRepository = new UserProfileRepositoryImpl[F](transactor)
      val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
      val passwordService = new PasswordServiceImpl[F]
      val registrationService = new RegistrationServiceImpl[F](userLoginDetailsRepository, passwordService)
      val authService = new AuthenticationServiceImpl[F](userLoginDetailsRepository, userRepository, passwordService)
      val redisTokenCommands = new RedisTokenCommands[F](redisCommands)
      val tokenService = new TokenServiceImpl[F](secretKey, clock, redisTokenCommands)
      val userController = new UserControllerImpl[F](authService, registrationService, tokenService)

      userController.routes
    }
  }

  def createBookingRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    val bookingRepository = new BookingRepository[F](transactor)
    val bookingService = new BookingServiceImpl[F](bookingRepository)
    val bookingController = new BookingControllerImpl[F](bookingService)

    bookingController.routes
  }

  def createBusinessRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    val businessRepository = new BusinessRepository[F](transactor)
    val businessService = new BusinessServiceImpl[F](businessRepository)
    val businessController = new BusinessControllerImpl[F](businessService)

    businessController.routes
  }

  def createWorkspaceRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    val workspaceRepository = new WorkspaceRepository[F](transactor)
    val workspaceService = new WorkspaceServiceImpl[F](workspaceRepository)
    val workspaceController = new WorkspaceControllerImpl[F](workspaceService)

    workspaceController.routes
  }
}
