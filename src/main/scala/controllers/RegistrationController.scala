//package controllers
//
//import cats.data.Validated.{Invalid, Valid}
//import cats.effect.Concurrent
//import cats.implicits.*
//import io.circe.syntax.EncoderOps
//import models.users.responses.*
//import models.users.{SignUpRequest, UserSignUpRequest, UserRegistrationRequest, UserRoleUpdateRequest}
//import org.http4s.*
//import org.http4s.circe.*
//import org.http4s.dsl.Http4sDsl
//import services.auth.algebra.*
//
//trait RegistrationControllerAlgebra[F[_]] {
//  def routes: HttpRoutes[F]
//}
//
//object RegistrationController {
//  def apply[F[_] : Concurrent](authService: AuthenticationServiceAlgebra[F],
//                               registrationService: RegistrationServiceAlgebra[F],
//                               tokenService: TokenServiceAlgebra[F]): RegistrationControllerAlgebra[F] =
//    new RegistrationControllerImpl[F](authService, registrationService, tokenService)
//}
//
//class RegistrationControllerImpl[F[_] : Concurrent](
//                                                     authService: AuthenticationServiceAlgebra[F],
//                                                     registrationService: RegistrationServiceAlgebra[F],
//                                                     tokenService: TokenServiceAlgebra[F]
//                                                   ) extends Http4sDsl[F] with RegistrationControllerAlgebra[F] {
//
//  implicit val signUpRequestDecoder: EntityDecoder[F, SignUpRequest] = jsonOf[F, SignUpRequest]
//
//  val routes: HttpRoutes[F] = HttpRoutes.of[F] {
//
//    // Sign up a new user
//    case req@POST -> Root / "signup" =>
//      req.decode[SignUpRequest] { request =>
//        registrationService.signUp(request).flatMap {
//          case Valid(user) =>
//            Created(LoginResponse("User sign up was successful").asJson)
//          case Invalid(errors) =>
//            BadRequest(ErrorUserResponse(errors).asJson)
//        }
//      }
//  }
//}
