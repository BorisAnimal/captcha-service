package captcha.controllers


import captcha.controllers.CaptchaRepository.IdError
import captcha.instances.Captcha.ID
import captcha.instances.{Captcha, Checker}
import captcha.instances.Checker._
import cats.effect._
import cats.syntax.either._
import org.http4s._
import org.http4s.dsl.io._

object AnswerChecker {

  val repository: CaptchaRepository.type = CaptchaRepository

  //  implicit def resultEncoder: EntityEncoder[IO, Checker] = ???
  object IdQueryParameterMatcher extends QueryParamDecoderMatcher[ID]("id")

  object AnswerQueryParameterMatcher extends QueryParamDecoderMatcher[Answer](name = "answer")

  implicit def captchaEncoder: EntityEncoder[IO, Result] = ???

  // TODO: handle missing value with Raise
  def checkAnswer(id: ID, answer: Answer): Either[CaptchaRepository.CheckerError, Result] =
    repository.get(id) match {
      case Some(value) => value.equals(answer).asRight
      case None => IdError.asLeft
    }

  //    repository.get(id).equals(answer)

  val checkerService: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "check" :? IdQueryParameterMatcher(id) +& AnswerQueryParameterMatcher(answer) =>
      checkAnswer(id, answer) match {
        case Right(value) => Ok(value)
        case Left(value) => BadRequest(value.toString)
      }
  }
}
