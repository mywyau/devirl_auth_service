package routes

import cats.NonEmptyParallel
import cats.effect.*
import controllers.*
import controllers.login.LoginControllerImpl
import controllers.registration.RegistrationControllerImpl
import controllers.wanderer_address.WandererAddressControllerImpl
import controllers.wanderer_profile.WandererProfileControllerImpl
import dev.profunktor.redis4cats.effect.Log
import doobie.hikari.HikariTransactor
import org.http4s.HttpRoutes
import repositories.*
import repositories.bookings.BookingRepository
import repositories.business.BusinessRepository
import repositories.users.{UserLoginDetailsRepositoryImpl, WandererAddressRepositoryImpl, WandererContactDetailsRepositoryImpl}
import repositories.workspaces.WorkspaceRepository
import services.*
import services.auth.AuthenticationServiceImpl
import services.bookings.BookingServiceImpl
import services.business.BusinessServiceImpl
import services.login.LoginServiceImpl
import services.password.PasswordServiceImpl
import services.registration.RegistrationServiceImpl
import services.wanderer_address.WandererAddressServiceImpl
import services.wanderer_profile.WandererProfileServiceImpl

object Routes {

  def wandererProfileRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {
    
    val userLoginDetailsRepo = new UserLoginDetailsRepositoryImpl[F](transactor)
    val wandererAddressRepo = new WandererAddressRepositoryImpl[F](transactor)
    val wandererContactDetailsrepo = new WandererContactDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]

    val wandererProfileService = new WandererProfileServiceImpl[F](userLoginDetailsRepo, wandererAddressRepo, wandererContactDetailsrepo, passwordService)

    val wandererProfileController = new WandererProfileControllerImpl[F](wandererProfileService)

    wandererProfileController.routes
  }


  def registrationRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]
    val registrationService = new RegistrationServiceImpl[F](userLoginDetailsRepository, passwordService)
    val registrationController = new RegistrationControllerImpl[F](registrationService)

    registrationController.routes
  }

  def loginRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)

    val passwordService = new PasswordServiceImpl[F]
    val loginService = new LoginServiceImpl[F](userLoginDetailsRepository, passwordService)

    val loginController = new LoginControllerImpl[F](loginService)

    loginController.routes
  }

  def wandererAddressRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val wandererAddressImplRepository = new WandererAddressRepositoryImpl[F](transactor)
    val wandererAddressService = new WandererAddressServiceImpl[F](wandererAddressImplRepository)

    val wandererAddressController = new WandererAddressControllerImpl[F](wandererAddressService)

    wandererAddressController.routes
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
