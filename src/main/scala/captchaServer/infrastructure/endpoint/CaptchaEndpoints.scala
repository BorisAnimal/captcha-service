package captchaServer.infrastructure.endpoint

import java.io.ByteArrayOutputStream
import java.util.Base64

import captchaServer.domain.captcha.{CaptchaService, CaptchaToImageTransformer}
import cats.effect.{Blocker, Sync}
import cats.implicits._
import io.circe.syntax._
import javax.imageio.ImageIO
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

import scala.concurrent.ExecutionContext.global

class CaptchaEndpoints[F[_] : Sync] extends Http4sDsl[F] {
  val blocker: Blocker = Blocker.liftExecutionContext(global)

  def endpoints(captchaService: CaptchaService[F], transformer: CaptchaToImageTransformer[F]): HttpRoutes[F] = {
    checkCaptchaEndpoint(captchaService) <+> generateCaptchaEndpoint(captchaService, transformer)
  }

  private def checkCaptchaEndpoint(captchaService: CaptchaService[F]): HttpRoutes[F] = HttpRoutes.of[F] {
    case request@GET -> Root / "check" :? IdQueryParameterMatcher(id) +& AnswerQueryParameterMatcher(answer) =>
      captchaService.checkCaptcha(id, answer).value.flatMap {
        case Right(value) => Ok(value.asJson)
        case Left(error) => NotFound(error.toString.asJson)
      }
  }

  private def generateCaptchaEndpoint(captchaService: CaptchaService[F],
                                      transformer: CaptchaToImageTransformer[F]): HttpRoutes[F] =
    HttpRoutes.of[F] {
      case GET -> Root / "generate" =>
        captchaService.generateCaptcha.flatMap(cap => {
          val maybeImg = transformer.transform(cap.answer)
          maybeImg.value.flatMap {
            case Left(value) => NotFound() //TODO: Log value
            case Right(img) =>
              val os = new ByteArrayOutputStream
              ImageIO.write(img, "png", os)
              val b64 = Base64.getEncoder.encodeToString(os.toByteArray)
              Ok(s"""{"id": ${cap.id.get}, "image": "$b64"}""")
          }
        })
    }

  object IdQueryParameterMatcher extends QueryParamDecoderMatcher[Int](name = "id")

  object AnswerQueryParameterMatcher extends QueryParamDecoderMatcher[String](name = "answer")

}

object CaptchaEndpoints {
  def endpoints[F[_] : Sync](captchaService: CaptchaService[F],
                             transformer: CaptchaToImageTransformer[F]): HttpRoutes[F] =
    new CaptchaEndpoints[F].endpoints(captchaService, transformer)
}
