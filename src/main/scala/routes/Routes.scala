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
    // Create repositories, services, and controllers as needed
    val userRepository = new UserRepositoryImpl[F](transactor)
    val authService = new AuthenticationServiceImpl[F](userRepository)
    val userController = new UserControllerImpl[F](authService)

    // Return the routes defined by the user controller
    userController.routes
  }

  def createBookingRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    // Repositories, services, and controllers setup as before
    val bookingRepository = new BookingRepository[F](transactor)
    val bookingService = new BookingServiceImpl[F](bookingRepository)
    val bookingController = new BookingControllerImpl[F](bookingService)

    bookingController.routes
  }

  def createBusinessRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    // Repositories, services, and controllers setup as before
    val businessRepository = new BusinessRepository[F](transactor)
    val businessService = new BusinessServiceImpl[F](businessRepository)
    val businessController = new BusinessControllerImpl[F](businessService)

    businessController.routes
  }

  def createWorkspaceRoutes[F[_] : Concurrent : Temporal](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    // Repositories, services, and controllers setup as before
    val workspaceRepository = new WorkspaceRepository[F](transactor)
    val workspaceService = new WorkspaceServiceImpl[F](workspaceRepository)
    val workspaceController = new WorkspaceControllerImpl[F](workspaceService)

    workspaceController.routes
  }
}
