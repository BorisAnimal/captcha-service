package captchaServer.infrastructure.endpoint

import captchaServer.domain.captcha.CaptchaService
import cats.effect.Sync
import cats.implicits._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes

class CaptchaEndpoints[F[_] : Sync] extends Http4sDsl[F] {

  def endpoints(captchaService: CaptchaService[F]): HttpRoutes[F] = {
    checkCaptchaEndpoint(captchaService) <+> generateCaptchaEndpoint(captchaService)
  }

  private def checkCaptchaEndpoint(captchaService: CaptchaService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case request@GET -> Root / "check" :? IdQueryParameterMatcher(id) +& AnswerQueryParameterMatcher(answer) =>
      captchaService.checkCaptcha(id, answer).value.flatMap {
        case Right(value) => Ok(value.asJson)
        case Left(error) => NotFound(error.toString.asJson)
      }
  }

  private def generateCaptchaEndpoint(captchaService: CaptchaService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "generate" =>
      //TODO: add wrapping to image and JSON and etc
      for {
        cap <- captchaService.generateCaptcha
        resp <- Ok(cap.asJson)
      } yield resp
  }

  object IdQueryParameterMatcher extends QueryParamDecoderMatcher[Int](name = "id")

  object AnswerQueryParameterMatcher extends QueryParamDecoderMatcher[String](name = "answer")

}

object CaptchaEndpoints {
  def endpoints[F[_] : Sync](captchaService: CaptchaService[F]): HttpRoutes[F] =
    new CaptchaEndpoints[F].endpoints(captchaService)
}
