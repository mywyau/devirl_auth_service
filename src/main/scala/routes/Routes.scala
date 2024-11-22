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
import repositories.user_profile.{UserLoginDetailsRepositoryImpl, WandererAddressRepositoryImpl, WandererPersonalDetailsRepositoryImpl}
import services.*
import services.authentication.login.LoginServiceImpl
import services.authentication.password.PasswordServiceImpl
import services.authentication.registration.RegistrationServiceImpl
import services.wanderer_address.WandererAddressServiceImpl
import services.wanderer_profile.WandererProfileServiceImpl

object Routes {
  
  def registrationRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userLoginDetailsRepository = new UserLoginDetailsRepositoryImpl[F](transactor)
    val wandererAddressRepo = new WandererAddressRepositoryImpl[F](transactor)
    val wandererPersonalDetailsRepo = new WandererPersonalDetailsRepositoryImpl[F](transactor)

    val passwordService = new PasswordServiceImpl[F]
    val registrationService = new RegistrationServiceImpl[F](userLoginDetailsRepository, wandererAddressRepo, wandererPersonalDetailsRepo, passwordService)
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

  def wandererProfileRoutes[F[_] : Concurrent : Temporal : NonEmptyParallel : Async : Log](transactor: HikariTransactor[F]): HttpRoutes[F] = {

    val userLoginDetailsRepo = new UserLoginDetailsRepositoryImpl[F](transactor)
    val wandererAddressRepo = new WandererAddressRepositoryImpl[F](transactor)
    val wandererPersonalDetailsRepo = new WandererPersonalDetailsRepositoryImpl[F](transactor)
    val passwordService = new PasswordServiceImpl[F]

    val wandererProfileService = new WandererProfileServiceImpl[F](userLoginDetailsRepo, wandererAddressRepo, wandererPersonalDetailsRepo, passwordService)

    val wandererProfileController = new WandererProfileControllerImpl[F](wandererProfileService)

    wandererProfileController.routes
  }
}
