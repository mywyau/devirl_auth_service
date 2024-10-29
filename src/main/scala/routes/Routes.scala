package routes

import cats.NonEmptyParallel
import cats.effect._
import cats.syntax.all._
import controllers._
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import repositories._
import repositories.business.BusinessRepository
import repositories.workspaces.WorkspaceRepository
import services._

object Routes {

  def createAuthRoutes[F[_]: Concurrent: Temporal: NonEmptyParallel](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    val userRepository = new UserRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]
    val registrationService = new RegistrationServiceImpl[F](userRepository, passwordService)
    val authService = new AuthenticationServiceImpl[F](userRepository, passwordService)
    val userController = new UserControllerImpl[F](authService, registrationService)
    
    userController.routes
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
