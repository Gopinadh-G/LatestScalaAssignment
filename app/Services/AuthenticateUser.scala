package Services

import javax.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import Services.EarthService

import scala.concurrent.{ExecutionContext, Future}

case class UserRequest[A](val userName: String, val request: Request[A])
  extends WrappedRequest[A](request)

class AuthenticateUser  @Inject() (repo : EarthService,parser: BodyParsers.Default)(implicit ec: ExecutionContext)
  extends ActionBuilderImpl(parser) {

  override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    val userName: String = request.headers.get("username").fold("")(identity)

    if (/*repo.getAllUsers().contains(userName)*/userName.equalsIgnoreCase("admin")) {
      block(UserRequest(userName, request))
    } else {
      Future.successful(Results.Unauthorized("Unauthorized access !!"))
    }
  }
}
