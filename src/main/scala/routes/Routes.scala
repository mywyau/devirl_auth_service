package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import controllers.login.LoginControllerImpl
import controllers.registration.RegistrationControllerImpl
import dev.profunktor.redis4cats.effect.Log
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import repositories.*
import repositories.bookings.BookingRepository
import repositories.business.BusinessRepository
import repositories.users.{UserLoginDetailsRepositoryImpl, UserProfileRepositoryImpl}
import repositories.workspaces.WorkspaceRepository
import services.*
import services.auth.AuthenticationServiceImpl
import services.bookings.BookingServiceImpl
import services.business.BusinessServiceImpl
import services.login.LoginServiceImpl
import services.password.PasswordServiceImpl
import services.registration.RegistrationServiceImpl

object Routes {

  def createAuthRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userRepository = new UserProfileRepositoryImpl[F](transactor)
    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]

    val registrationService = new RegistrationServiceImpl[F](userLoginDetailsRepository, passwordService)
    val authService = new AuthenticationServiceImpl[F](userLoginDetailsRepository, userRepository, passwordService)
    val wandererProfileController = new WandererProfileControllerImpl[F](authService, registrationService)

    wandererProfileController.routes
  }


  def registrationRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userRepository = new UserProfileRepositoryImpl[F](transactor)
    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]

    val registrationService = new RegistrationServiceImpl[F](userLoginDetailsRepository, passwordService)
    val authService = new AuthenticationServiceImpl[F](userLoginDetailsRepository, userRepository, passwordService)
    val registrationController = new RegistrationControllerImpl[F](registrationService)

    registrationController.routes
  }

  def loginRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userRepository = new UserProfileRepositoryImpl[F](transactor)
    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]

    val loginService = new LoginServiceImpl[F](userLoginDetailsRepository, passwordService)
    val loginController = new LoginControllerImpl[F](loginService)

    loginController.routes
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
